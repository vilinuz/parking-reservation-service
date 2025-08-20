CREATE TABLE IF NOT EXISTS parking_lot_reservation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,        -- possible values NEW, ACTIVE, COMPLETED
    vehicle_plate VARCHAR(20) NOT NULL,
    lot_id VARCHAR(3)
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_lot_dates_status ON parking_lot_reservation (lot_id, start_time, end_time, status);
