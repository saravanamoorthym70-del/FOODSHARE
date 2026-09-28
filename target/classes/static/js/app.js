const apiRoot = "/api";
const state = { donors: [], ngos: [], listings: [], claims: [], report: null };
const $ = (selector, root = document) => root.querySelector(selector);

function escapeHtml(value = "") {
    return String(value).replace(/[&<>"']/g, (character) => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
    })[character]);
}

async function api(path, options = {}) {
    const response = await fetch(`${apiRoot}${path}`, {
        ...options,
        headers: { "Content-Type": "application/json", ...options.headers }
    });
    if (response.status === 204) return null;
    const result = await response.json().catch(() => ({}));
    if (!response.ok) {
        const detail = result.message || result.error || result.detail || "The request could not be completed.";
        throw new Error(detail);
    }
    return result;
}

function showToast(message, kind = "success") {
    const toast = document.createElement("div");
    toast.className = `toast toast-${kind}`;
    toast.textContent = message;
    $("#toast-container").append(toast);
    window.setTimeout(() => toast.remove(), 4200);
}

function formatDate(value) {
    if (!value) return "Not recorded";
    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? "Not recorded" : new Intl.DateTimeFormat(undefined, {
        month: "short", day: "numeric", hour: "numeric", minute: "2-digit"
    }).format(date);
}

function localDateTimeInput(date) {
    const pad = (value) => String(value).padStart(2, "0");
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

function isStillSafe(listing) {
    return new Date(listing.safeToEatUntil).getTime() > Date.now();
}

function listingStatus(listing) {
    return listing.status === "AVAILABLE" && !isStillSafe(listing) ? "EXPIRED" : listing.status;
}

function statusBadge(status) {
    const normalized = (status || "unknown").toLowerCase();
    return `<span class="badge badge-${escapeHtml(normalized)}">${escapeHtml(status || "Unknown")}</span>`;
}

function ngoOptions() {
    if (!state.ngos.length) return '<option value="">Register an NGO to claim</option>';
    return `<option value="">Select NGO</option>${state.ngos.map((ngo) => `<option value="${ngo.id}">${escapeHtml(ngo.name)}</option>`).join("")}`;
}

function listingCard(listing) {
    const status = listingStatus(listing);
    const available = status === "AVAILABLE";
    const donorName = listing.donor?.organization || listing.donor?.name || "Community donor";
    const details = listing.description ? `<p>${escapeHtml(listing.description)}</p>` : "";
    return `<article class="listing-card">
        <div class="listing-card-top"><div><h3>${escapeHtml(listing.foodName)}</h3><p>${escapeHtml(donorName)} · ${escapeHtml(listing.foodType)}</p></div>${statusBadge(status)}</div>
        ${details}
        <div class="listing-meta"><span>${escapeHtml(listing.quantity)} ${escapeHtml(listing.unit)}</span><span>Safe until ${escapeHtml(formatDate(listing.safeToEatUntil))}</span><span>${escapeHtml(listing.donor?.address || "Pickup details on request")}</span></div>
        ${available ? `<div class="listing-actions"><select aria-label="Choose NGO for ${escapeHtml(listing.foodName)}" data-ngo-select>${ngoOptions()}</select><button class="btn btn-primary" type="button" data-claim="${listing.id}" ${state.ngos.length ? "" : "disabled"}>Claim</button></div>` : ""}
    </article>`;
}

function emptyState(message) {
    return `<div class="empty-state"><div class="icon" aria-hidden="true">○</div><p>${escapeHtml(message)}</p></div>`;
}

function activeClaims() {
    return state.claims.filter((claim) => claim.status === "ACTIVE");
}

function claimRow(claim) {
    const listing = claim.foodListing || {};
    const ngo = claim.ngo || {};
    const active = claim.status === "ACTIVE";
    return `<article class="claim-row"><div><h3>${escapeHtml(listing.foodName || `Listing #${listing.id || ""}`)}</h3><p>${escapeHtml(ngo.name || "NGO")} · Claimed ${escapeHtml(formatDate(claim.claimedAt))}</p>${statusBadge(claim.status)}</div>
        ${active ? `<div class="action-btns"><button class="btn btn-success btn-sm" type="button" data-collect="${claim.id}">Mark collected</button><button class="btn btn-outline btn-sm" type="button" data-cancel="${claim.id}">Cancel</button></div>` : ""}</article>`;
}

function renderListings() {
    const available = state.listings.filter((listing) => listingStatus(listing) === "AVAILABLE");
    $("#dashboard-listings").innerHTML = available.length ? available.slice(0, 4).map(listingCard).join("") : emptyState("No food available right now. New donations will show up here.");

    const search = $("#listing-search").value.trim().toLowerCase();
    const filter = $("#listing-filter").value;
    const filtered = state.listings.filter((listing) => {
        const status = listingStatus(listing);
        const text = `${listing.foodName} ${listing.foodType} ${listing.description || ""} ${listing.donor?.organization || ""} ${listing.donor?.address || ""}`.toLowerCase();
        return (filter === "ALL" || status === filter) && (!search || text.includes(search));
    });
    $("#listing-grid").innerHTML = filtered.length ? filtered.map(listingCard).join("") : emptyState("No listings match these filters.");
}

function renderClaims() {
    const claims = activeClaims();
    $("#dashboard-claims").innerHTML = claims.length ? claims.slice(0, 5).map(claimRow).join("") : emptyState("No pickups are waiting. Active claims will appear here.");
}

function renderPartners() {
    $("#donor-list").innerHTML = state.donors.length ? state.donors.map((donor) => `<article class="partner-row"><h3>${escapeHtml(donor.organization || donor.name)}</h3><p>${escapeHtml(donor.name)} · ${escapeHtml(donor.email)}</p><p>${escapeHtml(donor.phone)} · ${escapeHtml(donor.address)}</p></article>`).join("") : emptyState("No donors registered yet.");
    $("#ngo-list").innerHTML = state.ngos.length ? state.ngos.map((ngo) => `<article class="partner-row"><h3>${escapeHtml(ngo.name)}</h3><p>${escapeHtml(ngo.email)} · ${escapeHtml(ngo.phone)}</p><p>${escapeHtml(ngo.address)} · Reg. ${escapeHtml(ngo.registrationNumber)}</p></article>`).join("") : emptyState("No NGO partners registered yet.");
    $("#listing-donor").innerHTML = state.donors.length ? `<option value="">Choose donor</option>${state.donors.map((donor) => `<option value="${donor.id}">${escapeHtml(donor.organization || donor.name)}</option>`).join("")}` : '<option value="">Register a donor first</option>';
}

function renderReport() {
    if (!state.report) {
        $("#report-content").innerHTML = emptyState("Choose a month to view collected food and diverted quantity.");
        return;
    }
    $("#report-content").innerHTML = `<div class="report-summary"><div class="report-number"><strong>${escapeHtml(state.report.totalListingsCollected)}</strong><span>Listings collected</span></div><div class="report-number accent"><strong>${escapeHtml(state.report.totalQuantityDiverted)} ${escapeHtml(state.report.unit || "")}</strong><span>Quantity diverted</span></div></div>`;
}

function render() {
    const availableCount = state.listings.filter((listing) => listingStatus(listing) === "AVAILABLE").length;
    const reportQuantity = state.report?.totalQuantityDiverted ?? 0;
    $("#stat-available").textContent = availableCount;
    $("#stat-claims").textContent = activeClaims().length;
    $("#stat-partners").textContent = state.donors.length + state.ngos.length;
    $("#stat-diverted").textContent = reportQuantity;
    renderListings();
    renderClaims();
    renderPartners();
    renderReport();
}

async function loadReport() {
    const [year, month] = $("#report-month").value.split("-").map(Number);
    if (!year || !month) return;
    state.report = await api(`/reports/monthly?year=${year}&month=${month}`);
}

async function refreshData() {
    $("#connection-note").hidden = true;
    try {
        const [donors, ngos, listings, claims] = await Promise.all([
            api("/donors"), api("/ngos"), api("/food-listings"), api("/claims")
        ]);
        state.donors = donors;
        state.ngos = ngos;
        state.listings = listings;
        state.claims = claims;
        await loadReport();
        render();
    } catch (error) {
        $("#connection-note").textContent = `Could not load FoodShare data: ${error.message} Check that MySQL is running and restart the application.`;
        $("#connection-note").hidden = false;
        showToast(error.message, "error");
    }
}

function setView(view) {
    document.querySelectorAll(".view-panel").forEach((panel) => {
        panel.hidden = panel.id !== `${view}-view`;
    });
    document.querySelectorAll("[data-view]").forEach((button) => {
        button.classList.toggle("active", button.dataset.view === view);
    });
    if (window.location.hash !== `#${view}`) history.replaceState(null, "", `#${view}`);
}

async function submitJsonForm(form, path, successMessage, options = {}) {
    const values = Object.fromEntries(new FormData(form).entries());
    if (options.numberFields) options.numberFields.forEach((field) => { values[field] = Number(values[field]); });
    try {
        await api(path(values), { method: "POST", body: JSON.stringify(options.payload ? options.payload(values) : values) });
        form.closest("dialog").close();
        form.reset();
        showToast(successMessage);
        await refreshData();
    } catch (error) {
        showToast(error.message, "error");
    }
}

document.addEventListener("click", async (event) => {
    const openButton = event.target.closest("[data-open]");
    if (openButton) {
        const modal = document.getElementById(openButton.dataset.open);
        if (modal.id === "listing-modal") {
            const minimum = new Date(Date.now() + 60 * 60 * 1000);
            $("#safe-until").min = localDateTimeInput(new Date());
            $("#safe-until").value = localDateTimeInput(minimum);
        }
        modal.showModal();
    }
    const closeButton = event.target.closest("[data-close]");
    if (closeButton) closeButton.closest("dialog").close();
    const viewButton = event.target.closest("[data-view]");
    if (viewButton) setView(viewButton.dataset.view);

    const claimButton = event.target.closest("[data-claim]");
    if (claimButton) {
        const ngoId = claimButton.closest(".listing-card").querySelector("[data-ngo-select]").value;
        if (!ngoId) return showToast("Choose an NGO before claiming this listing.", "info");
        claimButton.disabled = true;
        try {
            await api(`/claims/listing/${claimButton.dataset.claim}/ngo/${ngoId}`, { method: "POST" });
            showToast("Listing claimed. Coordinate the pickup with the donor.");
            await refreshData();
        } catch (error) {
            showToast(error.message, "error");
            claimButton.disabled = false;
        }
    }

    const collectButton = event.target.closest("[data-collect]");
    if (collectButton) {
        try {
            await api(`/claims/${collectButton.dataset.collect}/collect`, { method: "PUT" });
            showToast("Pickup marked as collected.");
            await refreshData();
        } catch (error) { showToast(error.message, "error"); }
    }

    const cancelButton = event.target.closest("[data-cancel]");
    if (cancelButton) {
        try {
            await api(`/claims/${cancelButton.dataset.cancel}/cancel`, { method: "PUT" });
            showToast("Claim cancelled. The listing is available again.", "info");
            await refreshData();
        } catch (error) { showToast(error.message, "error"); }
    }
});

$("#donor-form").addEventListener("submit", (event) => {
    event.preventDefault();
    submitJsonForm(event.currentTarget, () => "/donors", "Donor registered.");
});

$("#ngo-form").addEventListener("submit", (event) => {
    event.preventDefault();
    submitJsonForm(event.currentTarget, () => "/ngos", "NGO registered.");
});

$("#listing-form").addEventListener("submit", (event) => {
    event.preventDefault();
    submitJsonForm(event.currentTarget, (values) => `/food-listings/donor/${values.donorId}`, "Food listing published.", {
        numberFields: ["quantity"],
        payload: (values) => ({
            foodName: values.foodName,
            foodType: values.foodType,
            quantity: values.quantity,
            unit: values.unit,
            safeToEatUntil: values.safeToEatUntil,
            description: values.description
        })
    });
});

$("#listing-search").addEventListener("input", renderListings);
$("#listing-filter").addEventListener("change", renderListings);
$("#load-report").addEventListener("click", async () => {
    try {
        await loadReport();
        render();
    } catch (error) { showToast(error.message, "error"); }
});

document.querySelectorAll("dialog.modal").forEach((modal) => {
    modal.addEventListener("click", (event) => {
        if (event.target === modal) modal.close();
    });
});

const currentDate = new Date();
$("#report-month").value = `${currentDate.getFullYear()}-${String(currentDate.getMonth() + 1).padStart(2, "0")}`;
const initialView = window.location.hash.slice(1);
setView(["dashboard", "listings", "partners", "reports"].includes(initialView) ? initialView : "dashboard");
refreshData();