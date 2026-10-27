# EntryWise — Seed Data Reference Guide

This reference documents all pre-seeded events, gates, seating maps, and test tickets loaded deterministically into EntryWise. Use these records to manually verify validation logic, test gate routing, and reproduce the debugging challenge issues.

---

## 1. Seeded Test Tickets & Verification Roster

Use these ticket identifiers in the **Ticket Validator** view (`/` -> Ticket Validator) to test scanner inputs:

| Ticket Code | Attendee Name | Ticket Tier | Assigned Event | Recommended Gate | Initial Status | Test Scenario / Purpose |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `EW-1001-VIP` | Elena Rostova | VIP | `EVT-101` | Gate 1 (Main North) | Active / Pending | **Bug 3 Test**: Valid VIP ticket scanning at Gate 1; verifies if unrestricted gate incorrectly rejects valid tickets. |
| `EW-1002-VIP` | Liam Chen | VIP | `EVT-101` | Gate 2 (VIP FastTrack) | Checked In | **Duplicate Entry**: Pre-admitted attendee (45m ago); tests duplicate scan prevention. |
| `EW-1003-PREM` | Chloe Bennett | Premium | `EVT-101` | Gate 1 (Main North) | Checked In | **Duplicate Entry**: Pre-admitted premium attendee (30m ago); tests re-entry prevention. |
| `EW-1004-STD` | David Miller | Standard | `EVT-101` | Gate 3 (West General) | Active / Pending | **Bug 2 Test**: Ticket requires automated seat allocation; tests if system erroneously assigns reserved seats. |
| `EW-1005-STD` | Sarah Jenkins | Standard | `EVT-101` | Gate 3 (West General) | Active / Pending | **Bug 1 Test**: Position #2 in queue behind David Miller; tests FIFO vs LIFO tie-breaker ordering. |
| `EW-1006-VIP` | Marcus Vance | VIP | `EVT-101` | Gate 1 (Main North) | Active / Pending | **Priority Queue**: VIP priority tier in sequence #3; tests whether VIP jumps ahead of standard tier queue. |
| `EW-9999-CAN` | Noah Wilson | Standard | `EVT-101` | Gate 1 (Main North) | Cancelled | **Invalid Credential**: Cancelled ticket; verifies immediate rejection with `TICKET_CANCELLED`. |
| `EW-8888-SUS` | Olivia Reed | Standard | `EVT-101` | Gate 1 (Main North) | Suspended | **Security Hold**: Suspended ticket; verifies security rejection with `TICKET_SUSPENDED`. |
| `EW-2001-GA` | Sophia Turner | General Admission | `EVT-102` | Gate 4 (Expo Hall A) | Active / Pending | **Wrong Event**: Valid ticket for TechHorizon Summit (`EVT-102`); tests event mismatch rejection on `EVT-101`. |
| `EW-UNKNOWN-99` | Unknown | Unknown | — | Gate 1 (Main North) | Invalid | **Barcode Not Found**: Non-existent ticket ID; tests 404 credential not found rejection. |

---

## 2. Seeded Entry Gates

| Gate ID | Gate Name | Event ID | Status | Allowed Categories | Description |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `GATE-01` | Gate 1 — Main North Plaza | `EVT-101` | Open | **Unrestricted** (All Tiers) | Main arena turnstile. Should allow VIP, Premium, and Standard tickets. |
| `GATE-02` | Gate 2 — VIP & FastTrack | `EVT-101` | Open | `VIP`, `PREMIUM` | Express turnstile restricted to VIP and Premium holders only. |
| `GATE-03` | Gate 3 — West General Entrance | `EVT-101` | Open | `STANDARD` | General turnstile restricted to Standard ticket holders. |
| `GATE-04` | Expo Hall A Entrance | `EVT-102` | Open | `GENERAL_ADMISSION` | Dedicated entrance for TechHorizon Summit 2026. |

---

## 3. Seeded Events & Capacity

| Event ID | Event Name | Venue | Total Capacity | Admitted | Buffer | Initial Remaining | Seating Mode |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `EVT-101` | Summer Soundwave 2026 | Bayfront Grand Arena | 500 | 2 | 50 | 448 (Bug: shows 450) | Assigned Seating |
| `EVT-102` | TechHorizon Summit 2026 | Metropolitan Expo Center | 800 | 0 | 80 | 720 | General Admission |

---

## 4. Seeded Seating Layout (Summer Soundwave 2026 — `EVT-101`)

### Section: VIP Stage Front (Row A)
- `SEAT-A01`: **Reserved** (Held for Elena Rostova, Ticket `EW-1001-VIP`)
- `SEAT-A02`: **Available**
- `SEAT-A03`: **Available**
- `SEAT-A04`: **Occupied** (Liam Chen, Ticket `EW-1002-VIP`)

### Section: Center Balcony (Row B)
- `SEAT-B01`: **Occupied** (Chloe Bennett, Ticket `EW-1003-PREM`)
- `SEAT-B02`: **Available**
- `SEAT-B03`: **Available**
- `SEAT-B04`: **Available**

### Section: Standard Tier (Row C)
- `SEAT-C01` through `SEAT-C06`: **Available**

---

## 5. Seeded Queue Lanes

### Gate 1 (`GATE-01`)
1. Sequence #1: **David Miller** (`EW-1004-STD`, Standard Tier, Arrived: -15 min)
2. Sequence #2: **Sarah Jenkins** (`EW-1005-STD`, Standard Tier, Arrived: -10 min)
3. Sequence #3: **Marcus Vance** (`EW-1006-VIP`, VIP Tier, Arrived: -5 min)

### Gate 3 (`GATE-03`)
1. Sequence #1: **David Miller** (`EW-1004-STD`, Standard Tier, Arrived: -12 min)
2. Sequence #2: **Sarah Jenkins** (`EW-1005-STD`, Standard Tier, Arrived: -8 min)
