/**
 * EntryWise — Intelligent Event Entry & Crowd Management Platform
 * Client Application Logic
 */

(function () {
    'use strict';

    // Application State
    const state = {
        currentEventId: 'EVT-101',
        currentGateId: 'GATE-01',
        events: [],
        activeEvent: null,
        gates: [],
        currentValidation: null,
        currentView: 'dashboard',
        seatingData: []
    };

    // DOM Elements
    const elements = {
        eventSelect: document.getElementById('event-select'),
        activeEventStatus: document.getElementById('active-event-status'),
        systemTime: document.getElementById('system-time'),
        mobileToggle: document.getElementById('btn-mobile-toggle'),
        sidebar: document.getElementById('sidebar'),
        btnResetScenario: document.getElementById('btn-reset-scenario'),
        btnRefreshDashboard: document.getElementById('btn-refresh-dashboard'),

        // Nav items
        navItems: document.querySelectorAll('.sidebar-nav .nav-item'),
        viewPanels: document.querySelectorAll('.view-panel'),

        // Metrics
        metricTotalCapacity: document.getElementById('metric-total-capacity'),
        metricAdmittedCount: document.getElementById('metric-admitted-count'),
        metricRemainingCapacity: document.getElementById('metric-remaining-capacity'),
        metricQueueCount: document.getElementById('metric-queue-count'),
        metricOccupancyRate: document.getElementById('metric-occupancy-rate'),
        metricActiveGates: document.getElementById('metric-active-gates'),
        occupancyProgressBar: document.getElementById('occupancy-progress-bar'),
        progressAdmittedDetail: document.getElementById('progress-admitted-detail'),
        progressPercentageDetail: document.getElementById('progress-percentage-detail'),
        progressRemainingDetail: document.getElementById('progress-remaining-detail'),
        statSuccessfulAdmissions: document.getElementById('stat-successful-admissions'),
        statRejectedAdmissions: document.getElementById('stat-rejected-admissions'),
        statTotalAttempts: document.getElementById('stat-total-attempts'),
        dashboardGatesList: document.getElementById('dashboard-gates-list'),
        tableRecentAdmissions: document.getElementById('table-recent-admissions').querySelector('tbody'),
        badgeVenueName: document.getElementById('badge-venue-name'),

        // Validation view
        formValidateTicket: document.getElementById('form-validate-ticket'),
        inputTicketCode: document.getElementById('input-ticket-code'),
        selectValidationGate: document.getElementById('select-validation-gate'),
        btnSubmitValidate: document.getElementById('btn-submit-validate'),
        validationEmptyState: document.getElementById('validation-empty-state'),
        validationDetailsContainer: document.getElementById('validation-details-container'),
        resultStatusBanner: document.getElementById('result-status-banner'),
        bannerStatusTitle: document.getElementById('banner-status-title'),
        bannerStatusMessage: document.getElementById('banner-status-message'),
        bannerStatusPill: document.getElementById('banner-status-pill'),
        docketAttendeeName: document.getElementById('docket-attendee-name'),
        docketAttendeeEmail: document.getElementById('docket-attendee-email'),
        docketTicketTier: document.getElementById('docket-ticket-tier'),
        docketAssignedSeat: document.getElementById('docket-assigned-seat'),
        btnAdmitAttendee: document.getElementById('btn-admit-attendee'),

        // Seating view
        seatingSectionsContainer: document.getElementById('seating-sections-container'),
        btnAllocateSeat: document.getElementById('btn-allocate-seat'),
        seatInspectorPanel: document.getElementById('seat-inspector-panel'),
        inspectorSeatLabel: document.getElementById('inspector-seat-label'),
        inspectorSeatBody: document.getElementById('inspector-seat-body'),
        btnCloseInspector: document.getElementById('btn-close-inspector'),

        // Gates view
        gateSelectionTabs: document.getElementById('gate-selection-tabs'),
        bannerGateName: document.getElementById('banner-gate-name'),
        bannerGateAllowed: document.getElementById('banner-gate-allowed'),
        bannerGateStatus: document.getElementById('banner-gate-status'),
        btnGateToggleOpen: document.getElementById('btn-gate-toggle-open'),
        btnGateTogglePaused: document.getElementById('btn-gate-toggle-paused'),
        btnGateToggleClosed: document.getElementById('btn-gate-toggle-closed'),
        btnProcessNextAttendee: document.getElementById('btn-process-next-attendee'),
        badgeQueueCount: document.getElementById('badge-queue-count'),
        queueLanesContainer: document.getElementById('queue-lanes-container'),

        // History view
        inputSearchHistory: document.getElementById('input-search-history'),
        filterHistoryOutcome: document.getElementById('filter-history-outcome'),
        tableFullHistory: document.getElementById('table-full-history').querySelector('tbody'),

        // Event view
        eventDetailsCard: document.getElementById('event-details-card'),
        tableTicketCategories: document.getElementById('table-ticket-categories').querySelector('tbody'),

        // Quick test chips
        scenarioChips: document.querySelectorAll('.scenario-chips .chip'),

        // Toast container
        toastContainer: document.getElementById('toast-container')
    };

    // Utility: Async with loading button state
    async function withLoading(button, asyncFn) {
        if (!button) return asyncFn();
        button.dataset.loading = 'true';
        button.disabled = true;
        try {
            return await asyncFn();
        } finally {
            button.dataset.loading = 'false';
            button.disabled = false;
        }
    }

    // Utility: Toast notification
    function showToast(message, type = 'success') {
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.innerHTML = `
            <span>${type === 'success' ? '✓' : '⚠'}</span>
            <span>${message}</span>
        `;
        elements.toastContainer.appendChild(toast);
        setTimeout(() => {
            toast.style.opacity = '0';
            setTimeout(() => toast.remove(), 300);
        }, 3500);
    }

    // Live UTC Clock
    function updateClock() {
        const now = new Date();
        elements.systemTime.textContent = now.toTimeString().split(' ')[0] + ' UTC';
    }
    setInterval(updateClock, 1000);
    updateClock();

    // Switch View
    function switchView(viewName) {
        state.currentView = viewName;
        elements.navItems.forEach(item => {
            item.classList.toggle('active', item.dataset.view === viewName);
        });
        elements.viewPanels.forEach(panel => {
            panel.classList.toggle('active', panel.id === `view-${viewName}`);
        });

        if (viewName === 'dashboard') loadDashboardMetrics();
        if (viewName === 'events') loadEventOverview();
        if (viewName === 'seating') loadSeatingLayout();
        if (viewName === 'gates') loadGateOperations();
        if (viewName === 'history') loadAdmissionHistory();
    }

    // Navigation Click Handlers
    elements.navItems.forEach(item => {
        item.addEventListener('click', () => {
            switchView(item.dataset.view);
            if (window.innerWidth <= 900) {
                elements.sidebar.classList.remove('open');
            }
        });
    });

    if (elements.mobileToggle) {
        elements.mobileToggle.addEventListener('click', () => {
            elements.sidebar.classList.toggle('open');
        });
    }

    document.getElementById('link-goto-gates').addEventListener('click', () => switchView('gates'));
    document.getElementById('link-goto-history').addEventListener('click', () => switchView('history'));

    // Fetch and populate events
    async function loadEvents() {
        try {
            const res = await fetch('api/events');
            if (!res.ok) throw new Error('Failed to load events');
            state.events = await res.json();

            elements.eventSelect.innerHTML = '';
            state.events.forEach(event => {
                const opt = document.createElement('option');
                opt.value = event.id;
                opt.textContent = `${event.name} (${event.id})`;
                elements.eventSelect.appendChild(opt);
            });

            if (state.events.length > 0) {
                state.currentEventId = state.events[0].id;
                state.activeEvent = state.events[0];
                elements.eventSelect.value = state.currentEventId;
                updateEventStatusHeader();
            }
        } catch (err) {
            console.error('loadEvents error:', err);
            showToast('Unable to load events', 'error');
        }
    }

    function updateEventStatusHeader() {
        if (!state.activeEvent) return;
        elements.activeEventStatus.textContent = state.activeEvent.status.replace(/_/g, ' ');
        if (elements.badgeVenueName) {
            elements.badgeVenueName.textContent = state.activeEvent.venue;
        }
    }

    // Change Active Event
    elements.eventSelect.addEventListener('change', async (e) => {
        state.currentEventId = e.target.value;
        state.activeEvent = state.events.find(ev => ev.id === state.currentEventId) || null;
        updateEventStatusHeader();
        await loadGatesForEvent();
        refreshCurrentView();
    });

    function refreshCurrentView() {
        switchView(state.currentView);
    }

    // Fetch gates for active event
    async function loadGatesForEvent() {
        try {
            const res = await fetch(`api/gates?eventId=${state.currentEventId}`);
            if (!res.ok) throw new Error('Failed to load gates');
            state.gates = await res.json();

            // Populate validation gate selector
            elements.selectValidationGate.innerHTML = '';
            state.gates.forEach(gate => {
                const opt = document.createElement('option');
                opt.value = gate.id;
                opt.textContent = `${gate.name} (${gate.status})`;
                elements.selectValidationGate.appendChild(opt);
            });

            if (state.gates.length > 0 && !state.gates.some(g => g.id === state.currentGateId)) {
                state.currentGateId = state.gates[0].id;
            }
        } catch (err) {
            console.error('loadGatesForEvent error:', err);
        }
    }

    // ==========================================
    // VIEW 1: DASHBOARD
    // ==========================================
    async function loadDashboardMetrics() {
        try {
            const res = await fetch(`api/metrics/${state.currentEventId}`);
            if (!res.ok) throw new Error('Failed to load metrics');
            const metrics = await res.json();

            elements.metricTotalCapacity.textContent = metrics.totalCapacity;
            elements.metricAdmittedCount.textContent = metrics.admittedCount;
            elements.metricRemainingCapacity.textContent = metrics.remainingCapacity;
            elements.metricQueueCount.textContent = metrics.waitingQueueCount;
            elements.metricOccupancyRate.textContent = `${metrics.occupancyPercentage}% occupancy`;
            elements.metricActiveGates.textContent = `${metrics.activeGatesCount} active gates`;

            elements.occupancyProgressBar.style.width = `${Math.min(100, metrics.occupancyPercentage)}%`;
            elements.progressAdmittedDetail.textContent = `${metrics.admittedCount} Admitted`;
            elements.progressPercentageDetail.textContent = `${metrics.occupancyPercentage}%`;
            elements.progressRemainingDetail.textContent = `${metrics.remainingCapacity} Remaining`;

            elements.statSuccessfulAdmissions.textContent = metrics.successfulAdmissions;
            elements.statRejectedAdmissions.textContent = metrics.rejectedAdmissions;
            elements.statTotalAttempts.textContent = metrics.successfulAdmissions + metrics.rejectedAdmissions;

            // Mini gate list
            elements.dashboardGatesList.innerHTML = '';
            state.gates.forEach(g => {
                const item = document.createElement('div');
                item.className = 'gate-mini-card';
                item.innerHTML = `
                    <span class="gate-mini-name">${g.name}</span>
                    <span class="badge ${g.status === 'OPEN' ? 'badge-success' : g.status === 'PAUSED' ? 'badge-warning' : 'badge-danger'}">${g.status}</span>
                `;
                elements.dashboardGatesList.appendChild(item);
            });

            // Recent history
            loadRecentAdmissions();
        } catch (err) {
            console.error('loadDashboardMetrics error:', err);
        }
    }

    async function loadRecentAdmissions() {
        try {
            const res = await fetch(`api/history?eventId=${state.currentEventId}`);
            if (!res.ok) throw new Error('Failed to load history');
            const records = await res.json();
            const recent = records.slice(0, 5);

            elements.tableRecentAdmissions.innerHTML = '';
            if (recent.length === 0) {
                elements.tableRecentAdmissions.innerHTML = '<tr><td colspan="6" style="text-align:center; padding:20px;">No admission attempts recorded yet.</td></tr>';
                return;
            }

            recent.forEach(r => {
                const tr = document.createElement('tr');
                const timeStr = r.timestamp ? new Date(r.timestamp).toLocaleTimeString() : '--';
                const isAdmitted = r.outcome === 'ADMITTED';
                tr.innerHTML = `
                    <td>${timeStr}</td>
                    <td><strong>${r.attendeeName || 'Unknown'}</strong></td>
                    <td><code>${r.ticketCode || '--'}</code></td>
                    <td>${r.gateId || '--'}</td>
                    <td><span class="badge ${isAdmitted ? 'badge-success' : 'badge-danger'}">${r.outcome}</span></td>
                    <td>${r.rejectionReason || 'Access Granted'}</td>
                `;
                elements.tableRecentAdmissions.appendChild(tr);
            });
        } catch (err) {
            console.error('loadRecentAdmissions error:', err);
        }
    }

    elements.btnRefreshDashboard.addEventListener('click', () => {
        withLoading(elements.btnRefreshDashboard, async () => {
            await loadDashboardMetrics();
            showToast('Operational telemetry updated');
        });
    });

    // ==========================================
    // VIEW 2: EVENT OVERVIEW
    // ==========================================
    async function loadEventOverview() {
        try {
            const res = await fetch(`api/events/${state.currentEventId}`);
            if (!res.ok) throw new Error('Failed to load event');
            const event = await res.json();

            const dateStr = event.eventDate ? new Date(event.eventDate).toLocaleDateString(undefined, { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' }) : '--';
            const windowStr = event.admissionWindowStart && event.admissionWindowEnd
                ? `${new Date(event.admissionWindowStart).toLocaleTimeString()} – ${new Date(event.admissionWindowEnd).toLocaleTimeString()}`
                : 'Live Operations';

            elements.eventDetailsCard.innerHTML = `
                <div>
                    <h2 style="font-size: 1.4rem; font-weight: 800; margin-bottom: 8px;">${event.name}</h2>
                    <p style="color: var(--text-secondary); margin-bottom: 16px;">${event.venue}</p>
                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px; font-size: 0.88rem;">
                        <div><strong style="color:var(--text-muted)">DATE:</strong> <div>${dateStr}</div></div>
                        <div><strong style="color:var(--text-muted)">ADMISSION WINDOW:</strong> <div>${windowStr}</div></div>
                        <div><strong style="color:var(--text-muted)">SEATING MODE:</strong> <div>${event.assignedSeating ? 'Assigned Row & Seat' : 'General Admission Capacity'}</div></div>
                        <div><strong style="color:var(--text-muted)">CONFIGURED CAPACITY:</strong> <div>${event.totalCapacity} attendees</div></div>
                    </div>
                </div>
                <div style="background: var(--bg-surface-elevated); padding: 20px; border-radius: var(--radius-sm); display: flex; flex-direction: column; justify-content: center; gap: 10px;">
                    <div style="font-size: 0.75rem; font-weight: 700; color: var(--text-muted);">EVENT STATUS</div>
                    <div style="font-size: 1.2rem; font-weight: 800; color: var(--emerald-500);">${event.status.replace(/_/g, ' ')}</div>
                    <div style="font-size: 0.8rem; color: var(--text-secondary);">Currently monitoring ${state.gates.length} operational entrance gates.</div>
                </div>
            `;

            // Ticket tiers table
            elements.tableTicketCategories.innerHTML = '';
            (event.ticketCategories || []).forEach(cat => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td><strong>${cat}</strong></td>
                    <td>Tier Category</td>
                    <td>${event.assignedSeating ? 'Designated Seat' : 'Free Standing Area'}</td>
                    <td>${cat === 'VIP' ? 'Gate 2 FastTrack' : cat === 'PREMIUM' ? 'Gate 1 & 2' : 'Gate 1 & 3'}</td>
                    <td><span class="badge badge-success">ACTIVE</span></td>
                `;
                elements.tableTicketCategories.appendChild(tr);
            });
        } catch (err) {
            console.error('loadEventOverview error:', err);
        }
    }

    // ==========================================
    // VIEW 3: TICKET VALIDATION
    // ==========================================
    elements.formValidateTicket.addEventListener('submit', async (e) => {
        e.preventDefault();
        const code = elements.inputTicketCode.value.trim();
        const gateId = elements.selectValidationGate.value;
        if (!code) return;

        await withLoading(elements.btnSubmitValidate, async () => {
            try {
                const res = await fetch('api/tickets/validate', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        ticketCode: code,
                        eventId: state.currentEventId,
                        gateId: gateId
                    })
                });
                if (!res.ok) throw new Error('Ticket validation failed');
                const result = await res.json();
                state.currentValidation = result;
                renderValidationResult(result);
            } catch (err) {
                console.error('validate error:', err);
                showToast('Validation request failed', 'error');
            }
        });
    });

    function renderValidationResult(res) {
        elements.validationEmptyState.style.display = 'none';
        elements.validationDetailsContainer.style.display = 'block';

        const isValid = res.valid;
        elements.resultStatusBanner.className = `result-status-banner ${isValid ? '' : 'rejected'}`;
        elements.bannerStatusTitle.textContent = isValid ? 'VALID FOR ADMISSION' : res.status.replace(/_/g, ' ');
        elements.bannerStatusMessage.textContent = res.message;
        elements.bannerStatusPill.className = `badge ${isValid ? 'badge-success' : 'badge-danger'}`;
        elements.bannerStatusPill.textContent = res.status;

        const ticket = res.ticket;
        const attendee = res.attendee;
        const seat = res.seat;

        elements.docketAttendeeName.textContent = attendee ? attendee.name : (ticket ? 'Attendee Record Found' : 'Unknown');
        elements.docketAttendeeEmail.textContent = attendee ? attendee.email : '--';
        elements.docketTicketTier.textContent = ticket ? ticket.category : '--';
        elements.docketAssignedSeat.textContent = seat ? seat.label : (ticket && ticket.seatId ? ticket.seatId : 'General Admission');

        // Admit button enabled state
        if (isValid) {
            elements.btnAdmitAttendee.style.display = 'inline-flex';
            elements.btnAdmitAttendee.disabled = false;
        } else {
            elements.btnAdmitAttendee.style.display = 'none';
        }
    }

    // Confirm Admission Click
    elements.btnAdmitAttendee.addEventListener('click', async () => {
        if (!state.currentValidation || !state.currentValidation.ticket) return;

        const code = state.currentValidation.ticket.ticketCode;
        const gateId = elements.selectValidationGate.value;

        await withLoading(elements.btnAdmitAttendee, async () => {
            try {
                const res = await fetch('api/checkin', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        ticketCode: code,
                        eventId: state.currentEventId,
                        gateId: gateId
                    })
                });

                if (!res.ok) throw new Error('Check-in failed');
                const record = await res.json();

                if (record.outcome === 'ADMITTED') {
                    showToast(`Attendee admitted successfully via ${record.gateId}`);
                    // Refresh validation result to show checked in
                    state.currentValidation.valid = false;
                    state.currentValidation.status = 'ALREADY_CHECKED_IN';
                    state.currentValidation.message = `Successfully checked in at ${new Date(record.timestamp).toLocaleTimeString()}`;
                    renderValidationResult(state.currentValidation);
                    // Sync dashboard & history
                    loadDashboardMetrics();
                } else {
                    showToast(`Admission rejected: ${record.outcome} - ${record.rejectionReason}`, 'error');
                    state.currentValidation.valid = false;
                    state.currentValidation.status = record.outcome;
                    state.currentValidation.message = record.rejectionReason;
                    renderValidationResult(state.currentValidation);
                }
            } catch (err) {
                console.error('checkin error:', err);
                showToast('Admission transaction failed', 'error');
            }
        });
    });

    // Quick test chips
    elements.scenarioChips.forEach(chip => {
        chip.addEventListener('click', () => {
            elements.inputTicketCode.value = chip.dataset.code;
            if (chip.dataset.gate && state.gates.some(g => g.id === chip.dataset.gate)) {
                elements.selectValidationGate.value = chip.dataset.gate;
            }
            elements.formValidateTicket.dispatchEvent(new Event('submit'));
        });
    });

    // Seeded test table "Load Code" buttons
    document.querySelectorAll('.chip-load').forEach(btn => {
        btn.addEventListener('click', () => {
            elements.inputTicketCode.value = btn.dataset.code;
            if (btn.dataset.gate && state.gates.some(g => g.id === btn.dataset.gate)) {
                elements.selectValidationGate.value = btn.dataset.gate;
            }
            switchView('validation');
            elements.formValidateTicket.dispatchEvent(new Event('submit'));
            elements.inputTicketCode.scrollIntoView({ behavior: 'smooth', block: 'center' });
            showToast(`Loaded ticket ${btn.dataset.code} for validation`);
        });
    });

    // ==========================================
    // VIEW 4: SEATING LAYOUT
    // ==========================================
    async function loadSeatingLayout() {
        try {
            const res = await fetch(`api/seating/${state.currentEventId}`);
            if (!res.ok) throw new Error('Failed to load seating');
            state.seatingData = await res.json();

            elements.seatingSectionsContainer.innerHTML = '';

            if (state.seatingData.length === 0) {
                elements.seatingSectionsContainer.innerHTML = `
                    <div class="empty-state">
                        <h4>General Admission Event</h4>
                        <p>This event does not use assigned seating. Attendees access open standing / seating areas with general admission tickets.</p>
                    </div>
                `;
                return;
            }

            // Group seats by Section then Row
            const bySection = {};
            state.seatingData.forEach(seat => {
                if (!bySection[seat.section]) bySection[seat.section] = {};
                if (!bySection[seat.section][seat.row]) bySection[seat.section][seat.row] = [];
                bySection[seat.section][seat.row].push(seat);
            });

            for (const [sectionName, rows] of Object.entries(bySection)) {
                const sectionEl = document.createElement('div');
                sectionEl.className = 'section-block';
                sectionEl.innerHTML = `<span class="section-label">${sectionName.toUpperCase()}</span>`;

                for (const [rowName, seats] of Object.entries(rows)) {
                    const rowEl = document.createElement('div');
                    rowEl.className = 'row-seats';
                    rowEl.innerHTML = `<span class="row-name">${rowName}</span>`;

                    seats.forEach(seat => {
                        const btn = document.createElement('button');
                        const statusClass = seat.status.toLowerCase();
                        btn.className = `seat-button ${statusClass}`;
                        btn.textContent = seat.seatNumber;
                        btn.title = `${seat.label} (${seat.status}) - ${seat.category}`;
                        btn.addEventListener('click', () => showSeatInspector(seat));
                        rowEl.appendChild(btn);
                    });

                    sectionEl.appendChild(rowEl);
                }

                elements.seatingSectionsContainer.appendChild(sectionEl);
            }
        } catch (err) {
            console.error('loadSeatingLayout error:', err);
        }
    }

    function showSeatInspector(seat) {
        elements.seatInspectorPanel.style.display = 'block';
        elements.inspectorSeatLabel.textContent = `Seat ${seat.label} Details`;
        elements.inspectorSeatBody.innerHTML = `
            <div style="font-size:0.85rem; display:flex; flex-direction:column; gap:8px;">
                <div><strong>SECTION:</strong> ${seat.section}</div>
                <div><strong>CATEGORY:</strong> ${seat.category}</div>
                <div><strong>STATUS:</strong> <span class="badge ${seat.status === 'AVAILABLE' ? 'badge-success' : seat.status === 'RESERVED' ? 'badge-warning' : 'badge-danger'}">${seat.status}</span></div>
                <div><strong>OCCUPANT:</strong> ${seat.attendeeName || 'None'}</div>
                <div><strong>TICKET ID:</strong> <code>${seat.ticketId || '--'}</code></div>
            </div>
        `;
    }

    elements.btnCloseInspector.addEventListener('click', () => {
        elements.seatInspectorPanel.style.display = 'none';
    });

    // Auto-allocate seat button
    elements.btnAllocateSeat.addEventListener('click', async () => {
        await withLoading(elements.btnAllocateSeat, async () => {
            try {
                const res = await fetch('api/seating/allocate', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        eventId: state.currentEventId,
                        ticketId: 'TCK-AUTO-' + Date.now(),
                        category: 'VIP'
                    })
                });

                if (!res.ok) {
                    const errJson = await res.json();
                    throw new Error(errJson.error || 'Allocation failed');
                }

                const allocatedSeat = await res.json();
                showToast(`Seat allocated: ${allocatedSeat.label} (${allocatedSeat.category})`);
                await loadSeatingLayout();
                showSeatInspector(allocatedSeat);
            } catch (err) {
                console.error('allocate error:', err);
                showToast(err.message, 'error');
            }
        });
    });

    // ==========================================
    // VIEW 5: GATES & QUEUE
    // ==========================================
    async function loadGateOperations() {
        await loadGatesForEvent();

        // Render gate tabs
        elements.gateSelectionTabs.innerHTML = '';
        state.gates.forEach(gate => {
            const tab = document.createElement('button');
            tab.className = `gate-tab ${gate.id === state.currentGateId ? 'active' : ''}`;
            tab.textContent = gate.name;
            tab.addEventListener('click', () => {
                state.currentGateId = gate.id;
                loadGateOperations();
            });
            elements.gateSelectionTabs.appendChild(tab);
        });

        const activeGate = state.gates.find(g => g.id === state.currentGateId) || state.gates[0];
        if (!activeGate) return;

        elements.bannerGateName.textContent = activeGate.name;
        const allowedCats = activeGate.allowedCategories && activeGate.allowedCategories.length > 0
            ? activeGate.allowedCategories.join(', ')
            : 'Unrestricted (All Categories)';
        elements.bannerGateAllowed.textContent = `Allowed Tiers: ${allowedCats}`;

        elements.bannerGateStatus.className = `gate-state-badge ${activeGate.status.toLowerCase()}`;
        elements.bannerGateStatus.textContent = activeGate.status;

        // Load waiting queue for active gate
        loadQueueLanes(activeGate.id);
    }

    async function loadQueueLanes(gateId) {
        try {
            const res = await fetch(`api/queue/${gateId}`);
            if (!res.ok) throw new Error('Failed to load queue');
            const queue = await res.json();

            elements.badgeQueueCount.textContent = `${queue.length} Waiting`;
            elements.queueLanesContainer.innerHTML = '';

            if (queue.length === 0) {
                elements.queueLanesContainer.innerHTML = `
                    <div class="empty-state">
                        <h4>Queue Lane Empty</h4>
                        <p>No attendees currently waiting in line at this gate entrance.</p>
                    </div>
                `;
                return;
            }

            queue.forEach((entry, index) => {
                const card = document.createElement('div');
                card.className = 'queue-card';
                card.innerHTML = `
                    <div class="queue-card-left">
                        <div class="queue-position-badge">#${index + 1}</div>
                        <div class="queue-attendee-info">
                            <span class="queue-name">${entry.attendeeName}</span>
                            <span class="queue-ticket-code">Code: <code>${entry.ticketCode}</code> &bull; Seq: ${entry.sequenceNumber}</span>
                        </div>
                    </div>
                    <div class="queue-card-right">
                        <span class="badge ${entry.priorityTier === 'VIP' ? 'badge-primary' : entry.priorityTier === 'PRIORITY' ? 'badge-warning' : 'badge-subtle'}">${entry.priorityTier}</span>
                        <span class="wait-time-indicator">~${entry.estimatedWaitMinutes} min wait</span>
                    </div>
                `;
                elements.queueLanesContainer.appendChild(card);
            });
        } catch (err) {
            console.error('loadQueueLanes error:', err);
        }
    }

    // Gate Status Mutations
    async function updateActiveGateStatus(newStatus) {
        try {
            const res = await fetch(`api/gates/${state.currentGateId}/status`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ status: newStatus })
            });
            if (!res.ok) throw new Error('Failed to update status');
            showToast(`Gate status updated to ${newStatus}`);
            await loadGateOperations();
        } catch (err) {
            console.error('updateGateStatus error:', err);
            showToast('Failed to change gate status', 'error');
        }
    }

    elements.btnGateToggleOpen.addEventListener('click', () => updateActiveGateStatus('OPEN'));
    elements.btnGateTogglePaused.addEventListener('click', () => updateActiveGateStatus('PAUSED'));
    elements.btnGateToggleClosed.addEventListener('click', () => updateActiveGateStatus('CLOSED'));

    // Process Next Attendee in Queue
    elements.btnProcessNextAttendee.addEventListener('click', async () => {
        await withLoading(elements.btnProcessNextAttendee, async () => {
            try {
                const res = await fetch(`api/queue/${state.currentGateId}/process-next`, {
                    method: 'POST'
                });

                if (res.status === 204) {
                    showToast('Queue is currently empty for this gate', 'error');
                    return;
                }

                if (!res.ok) throw new Error('Queue processing failed');
                const processed = await res.json();
                showToast(`Admitted: ${processed.attendeeName} (${processed.ticketCode})`);
                await loadQueueLanes(state.currentGateId);
                loadDashboardMetrics();
            } catch (err) {
                console.error('processNext error:', err);
                showToast('Failed to process attendee', 'error');
            }
        });
    });

    // ==========================================
    // VIEW 6: ATTENDEE & CHECK-IN HISTORY
    // ==========================================
    let allHistoryRecords = [];

    async function loadAdmissionHistory() {
        try {
            const res = await fetch(`api/history?eventId=${state.currentEventId}`);
            if (!res.ok) throw new Error('Failed to load history');
            allHistoryRecords = await res.json();
            renderHistoryTable();
        } catch (err) {
            console.error('loadAdmissionHistory error:', err);
        }
    }

    function renderHistoryTable() {
        const query = elements.inputSearchHistory.value.trim().toLowerCase();
        const outcomeFilter = elements.filterHistoryOutcome.value;

        const filtered = allHistoryRecords.filter(r => {
            const matchesQuery = !query ||
                (r.attendeeName && r.attendeeName.toLowerCase().includes(query)) ||
                (r.ticketCode && r.ticketCode.toLowerCase().includes(query)) ||
                (r.id && r.id.toLowerCase().includes(query));

            const matchesOutcome = outcomeFilter === 'ALL' ||
                (outcomeFilter === 'ADMITTED' && r.outcome === 'ADMITTED') ||
                (outcomeFilter === 'REJECTED' && r.outcome !== 'ADMITTED');

            return matchesQuery && matchesOutcome;
        });

        elements.tableFullHistory.innerHTML = '';
        if (filtered.length === 0) {
            elements.tableFullHistory.innerHTML = '<tr><td colspan="8" style="text-align:center; padding:24px;">No matching records found.</td></tr>';
            return;
        }

        filtered.forEach(r => {
            const tr = document.createElement('tr');
            const timeStr = r.timestamp ? new Date(r.timestamp).toLocaleTimeString() : '--';
            const isAdmitted = r.outcome === 'ADMITTED';

            const actionCell = isAdmitted
                ? `<button class="btn btn-sm btn-secondary btn-reverse" data-id="${r.id}">Reverse</button>`
                : `<span style="color:var(--text-muted); font-size:0.75rem;">None</span>`;

            tr.innerHTML = `
                <td><code>${r.id}</code></td>
                <td>${timeStr}</td>
                <td><strong>${r.attendeeName || 'Unknown'}</strong></td>
                <td><code>${r.ticketCode || '--'}</code></td>
                <td>${r.gateId || '--'}</td>
                <td><span class="badge ${isAdmitted ? 'badge-success' : 'badge-danger'}">${r.outcome}</span></td>
                <td>${r.rejectionReason || 'Access Granted'}</td>
                <td>${actionCell}</td>
            `;
            elements.tableFullHistory.appendChild(tr);
        });

        // Attach reverse handlers
        elements.tableFullHistory.querySelectorAll('.btn-reverse').forEach(btn => {
            btn.addEventListener('click', async () => {
                const recordId = btn.dataset.id;
                await withLoading(btn, async () => {
                    try {
                        const res = await fetch(`api/history/${recordId}/reverse`, {
                            method: 'POST'
                        });
                        if (!res.ok) throw new Error('Reversal failed');
                        showToast(`Admission record ${recordId} reversed`);
                        await loadAdmissionHistory();
                        loadDashboardMetrics();
                    } catch (err) {
                        console.error('reverse error:', err);
                        showToast('Failed to reverse admission', 'error');
                    }
                });
            });
        });
    }

    elements.inputSearchHistory.addEventListener('input', renderHistoryTable);
    elements.filterHistoryOutcome.addEventListener('change', renderHistoryTable);

    // ==========================================
    // RESET DEMO SCENARIO
    // ==========================================
    elements.btnResetScenario.addEventListener('click', async () => {
        await withLoading(elements.btnResetScenario, async () => {
            try {
                const res = await fetch('api/reset', { method: 'POST' });
                if (!res.ok) throw new Error('Reset request failed');
                showToast('Demonstration scenario reset successfully');
                await loadEvents();
                await loadGatesForEvent();
                refreshCurrentView();
            } catch (err) {
                console.error('reset error:', err);
                showToast('Reset failed', 'error');
            }
        });
    });

    // Background Telemetry Poller (Every 5 seconds)
    setInterval(() => {
        if (state.currentView === 'dashboard') {
            loadDashboardMetrics();
        } else if (state.currentView === 'gates') {
            loadQueueLanes(state.currentGateId);
        }
    }, 5000);

    // Initial Bootstrap
    async function init() {
        await loadEvents();
        await loadGatesForEvent();
        switchView('dashboard');
    }

    init();
})();
