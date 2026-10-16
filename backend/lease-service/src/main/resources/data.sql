INSERT INTO leases (
    org_id, resource_id, subject_id, created_at, updated_at, expires_at,
    max_uses, version, status
)
VALUES
    (1, 101, 201, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', 10, 0, 'ACTIVE'),
    (1, 102, 202, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', 10, 0, 'ACTIVE'),
    (1, 103, 203, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', 10, 0, 'ACTIVE'),
    (1, 104, 204, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', 10, 0, 'ACTIVE'),
    (1, 105, 205, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', 10, 0, 'ACTIVE');

INSERT INTO lease_permissions (lease_id, permission)
SELECT lease.id, seed.permission
FROM leases AS lease
JOIN (
    VALUES
        (101, 201, 'READ'),
        (101, 201, 'DOWNLOAD'),
        (102, 202, 'READ'),
        (103, 203, 'READ'),
        (103, 203, 'UPDATE'),
        (104, 204, 'DOWNLOAD'),
        (105, 205, 'READ'),
        (105, 205, 'DELETE')
) AS seed(resource_id, subject_id, permission)
    ON lease.org_id = 1
    AND lease.resource_id = seed.resource_id
    AND lease.subject_id = seed.subject_id;
