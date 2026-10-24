
#!/usr/bin/env bash
set -euo pipefail

mkdir -p trustlease
cd trustlease

generate() {
  local name="$1"
  local dependencies="$2"

  if [[ -e "$name" ]]; then
    echo "Skipping $name: it already exists."
    return
  fi

  echo "Generating $name..."

  curl -fsSLG https://start.spring.io/starter.zip \
    --data-urlencode "type=maven-project" \
    --data-urlencode "language=java" \
    --data-urlencode "javaVersion=21" \
    --data-urlencode "groupId=com.trustlease" \
    --data-urlencode "artifactId=$name" \
    --data-urlencode "name=$name" \
    --data-urlencode "packageName=com.trustlease.${name//-/}" \
    --data-urlencode "dependencies=$dependencies" \
    -o "$name.zip"

  mkdir -p "$name"
  unzip -q "$name.zip" -d "$name"
  rm "$name.zip"
}

generate identity-service \
  "web,security,oauth2-resource-server,data-jpa,postgresql,validation,flyway,actuator,lombok,devtools,docker-compose"

generate lease-service \
  "web,security,oauth2-resource-server,data-jpa,postgresql,validation,flyway,kafka,actuator,lombok,devtools,docker-compose"

generate idempotency-service \
  "web,security,oauth2-resource-server,data-redis,validation,actuator,lombok,devtools,docker-compose"

generate authorization-service \
  "web,security,oauth2-resource-server,data-jdbc,postgresql,data-redis,validation,actuator,lombok,devtools,docker-compose"

generate policy-service \
  "web,security,oauth2-resource-server,validation,actuator,lombok,devtools"

generate api-gateway \
  "cloud-gateway,security,oauth2-resource-server,actuator,lombok,devtools"

generate audit-service \
  "web,data-jpa,postgresql,flyway,kafka,actuator,lombok,devtools,docker-compose"

generate notification-service \
  "kafka,mail,actuator,lombok,devtools"

echo "TrustLease projects generated."
