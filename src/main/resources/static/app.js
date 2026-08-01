const API_BASE = 'http://localhost:8080/api';

// State
let currentUser = JSON.parse(localStorage.getItem('smartTaskUser')) || null;
let allTasks = [];
let allEmployees = [];
let allLeaves = [];

// OTP flow state
let otpPendingEmail = null;    // email waiting for OTP verification
let otpCountdownTimer = null;  // interval ID for countdown
let otpSecondsLeft = 300;      // 5 minutes

// DOM Ready
document.addEventListener('DOMContentLoaded', () => {
    initApp();

    // Event Listeners
    document.getElementById('loginForm').addEventListener('submit', handleLogin);
    document.getElementById('registerForm').addEventListener('submit', handleRegister);
    document.getElementById('applyLeaveForm').addEventListener('submit', handleApplyLeave);
    document.getElementById('addTaskForm').addEventListener('submit', handleSaveTask);
    document.getElementById('addEmployeeForm').addEventListener('submit', handleAddEmployee);
    document.getElementById('logoutBtn').addEventListener('click', handleLogout);
});

function initApp() {
    if (currentUser) {
        showDashboard();
    } else {
        showLandingPage();
    }
}

function showLandingPage() {
    document.getElementById('landingPage').classList.remove('hidden');
    document.getElementById('dashboardApp').classList.add('hidden');
}

function showDashboard() {
    document.getElementById('landingPage').classList.add('hidden');
    document.getElementById('dashboardApp').classList.remove('hidden');

    // Header info
    document.getElementById('welcomeUserName').textContent = currentUser.name.split(' ')[0];
    document.getElementById('topBarName').textContent = currentUser.name;
    document.getElementById('topBarRole').textContent = currentUser.role;
    document.getElementById('userAvatarCircle').textContent = currentUser.name.charAt(0).toUpperCase();

    // Role-specific UI adjustments
    if (currentUser.role === 'ADMIN') {
        document.getElementById('btnAdminAddTask').style.display = 'flex';
        document.getElementById('btnTasksTabAddTask').style.display = 'inline-block';
        document.getElementById('btnAddEmpDir').style.display = 'inline-block';
        document.getElementById('thAdminLeaveActions').style.display = 'table-cell';
    } else {
        document.getElementById('btnAdminAddTask').style.display = 'none';
        document.getElementById('btnTasksTabAddTask').style.display = 'none';
        document.getElementById('btnAddEmpDir').style.display = 'none';
        document.getElementById('thAdminLeaveActions').style.display = 'none';
    }

    // Load initial data
    loadDashboardData();
}

async function loadDashboardData() {
    await fetchAllTasks();
    await fetchAllLeaves();
    if (currentUser.role === 'ADMIN') {
        await fetchAllEmployees();
        await fetchStats();
    } else {
        renderProfileInfo();
    }
}

// --- NAVIGATION & SIDEBAR TABS ---

function switchSidebarTab(tabName) {
    const tabs = ['dashboard', 'profile', 'directory', 'tasks', 'leave', 'announcements'];
    tabs.forEach(t => {
        const navBtn = document.getElementById(`nav${capitalize(t)}`);
        const viewContent = document.getElementById(`view${capitalize(t)}`);
        
        if (t === tabName) {
            if (navBtn) navBtn.classList.add('active');
            if (viewContent) viewContent.classList.remove('hidden');
        } else {
            if (navBtn) navBtn.classList.remove('active');
            if (viewContent) viewContent.classList.add('hidden');
        }
    });

    if (tabName === 'tasks') loadAllTasks();
    if (tabName === 'leave') fetchAllLeaves();
    if (tabName === 'directory' && currentUser.role === 'ADMIN') fetchAllEmployees();
}

function capitalize(s) {
    return s.charAt(0).toUpperCase() + s.slice(1);
}

// --- AUTHENTICATION FLOW ---

function openAuthModal(tab) {
    switchAuthTab(tab);
    openModal('authModal');
}

function switchAuthTab(tab) {
    const loginBtn = document.getElementById('tabLoginBtn');
    const regBtn = document.getElementById('tabRegBtn');
    const loginForm = document.getElementById('loginForm');
    const regForm = document.getElementById('registerForm');

    if (tab === 'login') {
        loginBtn.classList.add('active');
        regBtn.classList.remove('active');
        loginForm.classList.remove('hidden');
        regForm.classList.add('hidden');
    } else {
        regBtn.classList.add('active');
        loginBtn.classList.remove('active');
        regForm.classList.remove('hidden');
        loginForm.classList.add('hidden');
    }
}

async function handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('loginEmail').value.trim();
    const password = document.getElementById('loginPassword').value.trim();
    const alertBox = document.getElementById('loginAlert');

    // Show spinner
    document.getElementById('loginBtnText').classList.add('hidden');
    document.getElementById('loginBtnSpinner').classList.remove('hidden');
    document.getElementById('loginSubmitBtn').disabled = true;

    try {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();
        if (!response.ok) throw new Error(data.message || 'Invalid email or password!');

        if (data.status === 'OTP_SENT') {
            // Step 1 success — show OTP modal
            otpPendingEmail = data.email;
            closeModal('authModal');
            openOtpModal(data.email);
        }
    } catch (err) {
        showAlert(alertBox, err.message, 'danger');
    } finally {
        document.getElementById('loginBtnText').classList.remove('hidden');
        document.getElementById('loginBtnSpinner').classList.add('hidden');
        document.getElementById('loginSubmitBtn').disabled = false;
    }
}

// --- OTP MODAL CONTROLS ---

function openOtpModal(email) {
    // Show email in modal
    document.getElementById('otpEmailDisplay').textContent = email;

    // Clear previous values + errors
    for (let i = 0; i < 6; i++) {
        const inp = document.getElementById(`otp${i}`);
        inp.value = '';
        inp.classList.remove('filled', 'otp-error');
    }
    document.getElementById('otpAlert').classList.add('hidden');

    // Reset button state
    document.getElementById('otpVerifyBtn').disabled = false;
    document.getElementById('otpVerifyBtnText').classList.remove('hidden');
    document.getElementById('otpVerifySpinner').classList.add('hidden');

    // Start countdown
    startOtpCountdown();

    // Show modal
    openModal('otpModal');

    // Setup digit keyboard navigation
    setupOtpDigitListeners();

    // Focus first box
    setTimeout(() => document.getElementById('otp0').focus(), 150);
}

function setupOtpDigitListeners() {
    for (let i = 0; i < 6; i++) {
        const inp = document.getElementById(`otp${i}`);
        inp.oninput = (e) => {
            // Allow only digits
            inp.value = inp.value.replace(/[^0-9]/g, '').slice(0, 1);
            inp.classList.toggle('filled', inp.value !== '');
            if (inp.value && i < 5) {
                document.getElementById(`otp${i + 1}`).focus();
            }
        };
        inp.onkeydown = (e) => {
            if (e.key === 'Backspace' && !inp.value && i > 0) {
                document.getElementById(`otp${i - 1}`).focus();
            }
            if (e.key === 'Enter') submitOtp();
            // Arrow key navigation
            if (e.key === 'ArrowRight' && i < 5) document.getElementById(`otp${i + 1}`).focus();
            if (e.key === 'ArrowLeft'  && i > 0) document.getElementById(`otp${i - 1}`).focus();
        };
        // Handle paste — fill all boxes from first digit
        inp.onpaste = (e) => {
            e.preventDefault();
            const pasted = (e.clipboardData || window.clipboardData).getData('text').replace(/\D/g, '').slice(0, 6);
            for (let j = 0; j < pasted.length; j++) {
                const box = document.getElementById(`otp${j}`);
                if (box) {
                    box.value = pasted[j];
                    box.classList.add('filled');
                }
            }
            const nextEmpty = Math.min(pasted.length, 5);
            document.getElementById(`otp${nextEmpty}`).focus();
        };
    }
}

function startOtpCountdown() {
    clearInterval(otpCountdownTimer);
    otpSecondsLeft = 300; // 5 minutes
    const countdownEl = document.getElementById('otpCountdown');
    const resendBtn = document.getElementById('otpResendBtn');

    // Reset styles
    countdownEl.className = 'otp-countdown';
    resendBtn.disabled = true;

    function tick() {
        const m = Math.floor(otpSecondsLeft / 60);
        const s = otpSecondsLeft % 60;
        countdownEl.textContent = `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;

        if (otpSecondsLeft <= 60)  countdownEl.classList.add('warning');
        if (otpSecondsLeft <= 0) {
            clearInterval(otpCountdownTimer);
            countdownEl.textContent = 'Expired';
            countdownEl.className = 'otp-countdown expired';
            document.getElementById('otpVerifyBtn').disabled = true;
            resendBtn.disabled = false;
            showAlert(document.getElementById('otpAlert'), 'OTP expired. Click Resend to get a new one.', 'danger');
            return;
        }
        otpSecondsLeft--;
    }

    tick();
    otpCountdownTimer = setInterval(tick, 1000);
}

async function submitOtp() {
    const otp = [0,1,2,3,4,5].map(i => document.getElementById(`otp${i}`).value).join('');
    const alertBox = document.getElementById('otpAlert');

    if (otp.length < 6) {
        showAlert(alertBox, 'Please enter all 6 digits.', 'danger');
        shakeOtpDigits();
        return;
    }

    // Show spinner
    document.getElementById('otpVerifyBtnText').classList.add('hidden');
    document.getElementById('otpVerifySpinner').classList.remove('hidden');
    document.getElementById('otpVerifyBtn').disabled = true;

    try {
        const response = await fetch(`${API_BASE}/auth/verify-otp`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email: otpPendingEmail, otp })
        });

        const data = await response.json();
        if (!response.ok) throw new Error(data.message || 'Invalid OTP. Please try again.');

        // Success — grant session
        clearInterval(otpCountdownTimer);
        currentUser = data;
        localStorage.setItem('smartTaskUser', JSON.stringify(currentUser));
        closeModal('otpModal');
        otpPendingEmail = null;
        showDashboard();
    } catch (err) {
        showAlert(alertBox, err.message, 'danger');
        shakeOtpDigits();
    } finally {
        document.getElementById('otpVerifyBtnText').classList.remove('hidden');
        document.getElementById('otpVerifySpinner').classList.add('hidden');
        document.getElementById('otpVerifyBtn').disabled = false;
    }
}

async function resendOtp() {
    if (!otpPendingEmail) return;
    const email = otpPendingEmail;
    const password = document.getElementById('loginPassword').value.trim();

    document.getElementById('otpResendBtn').disabled = true;

    try {
        // Re-hit login to regenerate OTP
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (!response.ok) throw new Error('Failed to resend OTP');

        // Reset digit boxes
        for (let i = 0; i < 6; i++) {
            const inp = document.getElementById(`otp${i}`);
            inp.value = '';
            inp.classList.remove('filled', 'otp-error');
        }
        document.getElementById('otpVerifyBtn').disabled = false;
        document.getElementById('otpAlert').classList.add('hidden');

        // Restart countdown
        startOtpCountdown();
        document.getElementById('otp0').focus();
        showAlert(document.getElementById('otpAlert'), '✅ New OTP sent to your email!', 'success');
        setTimeout(() => document.getElementById('otpAlert').classList.add('hidden'), 3000);
    } catch (err) {
        showAlert(document.getElementById('otpAlert'), 'Failed to resend OTP. Please go back and try again.', 'danger');
        document.getElementById('otpResendBtn').disabled = false;
    }
}

function backToLogin() {
    clearInterval(otpCountdownTimer);
    closeModal('otpModal');
    otpPendingEmail = null;
    openAuthModal('login');
}

function shakeOtpDigits() {
    for (let i = 0; i < 6; i++) {
        const inp = document.getElementById(`otp${i}`);
        inp.classList.add('otp-error');
        setTimeout(() => inp.classList.remove('otp-error'), 500);
    }
}

async function handleRegister(e) {
    e.preventDefault();
    const name = document.getElementById('regName').value.trim();
    const email = document.getElementById('regEmail').value.trim();
    const password = document.getElementById('regPassword').value.trim();
    const department = document.getElementById('regDepartment').value.trim();
    const role = document.getElementById('regRole').value;
    const alertBox = document.getElementById('registerAlert');

    try {
        const response = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password, department, role })
        });

        const data = await response.json();
        if (!response.ok) throw new Error(data.message || 'Registration failed!');

        showAlert(alertBox, 'Account created successfully! Please sign in.', 'success');
        setTimeout(() => switchAuthTab('login'), 1500);
    } catch (err) {
        showAlert(alertBox, err.message, 'danger');
    }
}

function handleLogout() {
    currentUser = null;
    localStorage.removeItem('smartTaskUser');
    showLandingPage();
}

// --- TASKS MODULE ---

async function fetchAllTasks() {
    try {
        const url = currentUser.role === 'ADMIN' 
            ? `${API_BASE}/admin/tasks` 
            : `${API_BASE}/employee/tasks/${currentUser.id}`;

        const res = await fetch(url);
        allTasks = await res.json();

        // Update dashboard metrics
        const pending = allTasks.filter(t => t.status === 'PENDING').length;
        const highPriority = allTasks.filter(t => t.priority === 'HIGH' && t.status !== 'COMPLETED').length;
        
        document.getElementById('cardPendingCount').textContent = pending;
        document.getElementById('cardHighPriorityCount').textContent = `${highPriority} high priority`;

        renderDashboardTasks(allTasks);
        renderTasksTable(allTasks);
    } catch (err) {
        console.error('Error fetching tasks:', err);
    }
}

function renderDashboardTasks(tasks) {
    const container = document.getElementById('dashboardTasksList');
    if (tasks.length === 0) {
        container.innerHTML = '<p style="color:var(--text-muted); font-size:0.88rem;">No upcoming tasks assigned.</p>';
        return;
    }

    container.innerHTML = tasks.slice(0, 3).map(t => `
        <div style="border-bottom:1px solid #e2e8f0; padding-bottom:0.8rem; margin-bottom:0.8rem; display:flex; justify-content:space-between; align-items:center;">
            <div>
                <strong style="font-size:0.9rem; display:block;">${t.title}</strong>
                <span style="font-size:0.78rem; color:var(--text-muted);">Due ${t.deadline || 'N/A'}</span>
            </div>
            <span class="status-pill status-${t.status ? t.status.toLowerCase() : 'pending'}">${t.status}</span>
        </div>
    `).join('');
}

function renderTasksTable(tasks) {
    const tbody = document.getElementById('tasksTableBody');
    if (tasks.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;">No tasks found.</td></tr>';
        return;
    }

    tbody.innerHTML = tasks.map(t => `
        <tr>
            <td>#${t.id}</td>
            <td>
                <strong>${t.title}</strong>
                <p style="font-size:0.78rem; color:var(--text-muted);">${t.description || ''}</p>
            </td>
            <td><span class="pill-tag tag-${t.priority === 'HIGH' ? 'purple' : 'gray'}">${t.priority}</span></td>
            <td>${t.deadline || 'N/A'}</td>
            <td><i class="fa-solid fa-user"></i> ${t.employeeName}</td>
            <td>
                ${currentUser.role === 'EMPLOYEE' ? `
                    <select class="input-group" style="padding:0.3rem; margin:0;" onchange="updateTaskStatus(${t.id}, this.value)">
                        <option value="PENDING" ${t.status === 'PENDING' ? 'selected' : ''}>PENDING</option>
                        <option value="IN_PROGRESS" ${t.status === 'IN_PROGRESS' ? 'selected' : ''}>IN PROGRESS</option>
                        <option value="COMPLETED" ${t.status === 'COMPLETED' ? 'selected' : ''}>COMPLETED</option>
                    </select>
                ` : `<span class="status-pill status-${t.status ? t.status.toLowerCase() : 'pending'}">${t.status}</span>`}
            </td>
            <td>
                ${currentUser.role === 'ADMIN' ? `
                    <button class="btn-action-sm" onclick="editTaskModal(${t.id})"><i class="fa-solid fa-pen"></i></button>
                    <button class="btn-action-sm btn-reject" onclick="deleteTask(${t.id})"><i class="fa-solid fa-trash"></i></button>
                ` : '-'}
            </td>
        </tr>
    `).join('');
}

async function updateTaskStatus(taskId, status) {
    try {
        await fetch(`${API_BASE}/employee/tasks/${taskId}/status`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status })
        });
        fetchAllTasks();
    } catch (err) {
        alert('Failed to update status');
    }
}

async function handleSaveTask(e) {
    e.preventDefault();
    const id = document.getElementById('modalTaskId').value;
    const title = document.getElementById('modalTaskTitle').value.trim();
    const description = document.getElementById('modalTaskDesc').value.trim();
    const priority = document.getElementById('modalTaskPriority').value;
    const deadline = document.getElementById('modalTaskDeadline').value;
    const employeeId = document.getElementById('modalTaskAssignee').value;

    const payload = { title, description, priority, deadline, employeeId: employeeId ? parseInt(employeeId) : null };
    const method = id ? 'PUT' : 'POST';
    const url = id ? `${API_BASE}/admin/tasks/${id}` : `${API_BASE}/admin/tasks`;

    try {
        const res = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!res.ok) throw new Error('Failed to save task');
        closeModal('addTaskModal');
        document.getElementById('addTaskForm').reset();
        fetchAllTasks();
    } catch (err) {
        alert(err.message);
    }
}

async function deleteTask(id) {
    if (!confirm('Are you sure you want to delete this task?')) return;
    await fetch(`${API_BASE}/admin/tasks/${id}`, { method: 'DELETE' });
    fetchAllTasks();
}

// --- LEAVE & ATTENDANCE MODULE (NEW FEATURE) ---

async function fetchAllLeaves() {
    try {
        const url = currentUser.role === 'ADMIN'
            ? `${API_BASE}/admin/leaves`
            : `${API_BASE}/employee/leaves/${currentUser.id}`;

        const res = await fetch(url);
        allLeaves = await res.json();

        const pendingCount = allLeaves.filter(l => l.status === 'PENDING').length;
        document.getElementById('cardLeaveCount').textContent = pendingCount;

        renderLeavesTable(allLeaves);
    } catch (err) {
        console.error('Error fetching leaves:', err);
    }
}

function renderLeavesTable(leaves) {
    const tbody = document.getElementById('leaveRequestsTableBody');
    if (leaves.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" style="text-align:center;">No leave requests found. Click "New Leave Request" to submit one.</td></tr>';
        return;
    }

    tbody.innerHTML = leaves.map(l => `
        <tr>
            <td>#${l.id}</td>
            <td><strong>${l.employeeName}</strong> <br><span style="font-size:0.75rem; color:var(--text-muted);">${l.employeeDept || 'Staff'}</span></td>
            <td><span class="pill-tag tag-purple">${l.leaveType}</span></td>
            <td>${l.startDate}</td>
            <td>${l.endDate}</td>
            <td>${l.reason}</td>
            <td>${l.appliedOn || 'N/A'}</td>
            <td><span class="status-pill status-${l.status ? l.status.toLowerCase() : 'pending'}">${l.status}</span></td>
            ${currentUser.role === 'ADMIN' ? `
                <td>
                    ${l.status === 'PENDING' ? `
                        <button class="btn-action-sm btn-approve" onclick="updateLeaveStatus(${l.id}, 'APPROVED')"><i class="fa-solid fa-check"></i> Accept</button>
                        <button class="btn-action-sm btn-reject" onclick="updateLeaveStatus(${l.id}, 'REJECTED')"><i class="fa-solid fa-xmark"></i> Reject</button>
                    ` : '<span style="font-size:0.8rem; color:var(--text-muted);">Resolved</span>'}
                </td>
            ` : ''}
        </tr>
    `).join('');
}

async function handleApplyLeave(e) {
    e.preventDefault();
    const leaveType = document.getElementById('leaveType').value;
    const startDate = document.getElementById('leaveStartDate').value;
    const endDate = document.getElementById('leaveEndDate').value;
    const reason = document.getElementById('leaveReason').value.trim();

    const payload = {
        leaveType,
        startDate,
        endDate,
        reason,
        employeeId: currentUser.id
    };

    try {
        const res = await fetch(`${API_BASE}/employee/leaves`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!res.ok) throw new Error('Failed to submit leave request');

        closeModal('applyLeaveModal');
        document.getElementById('applyLeaveForm').reset();
        alert('🎉 Leave request submitted successfully! Your administrator can now accept or reject it.');
        fetchAllLeaves();
    } catch (err) {
        alert(err.message);
    }
}

async function updateLeaveStatus(leaveId, status) {
    try {
        const res = await fetch(`${API_BASE}/admin/leaves/${leaveId}/status`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status })
        });

        if (!res.ok) throw new Error('Failed to update leave status');
        fetchAllLeaves();
    } catch (err) {
        alert(err.message);
    }
}

// --- EMPLOYEE DIRECTORY & PROFILE ---

async function fetchStats() {
    try {
        const res = await fetch(`${API_BASE}/admin/stats`);
        if (!res.ok) return;
        const stats = await res.json();

        // Update metric cards with live data from backend
        if (stats.pendingTasks !== undefined) {
            document.getElementById('cardPendingCount').textContent = stats.pendingTasks;
            document.getElementById('cardHighPriorityCount').textContent =
                `${stats.inProgressTasks ?? 0} in progress`;
        }
        if (stats.pendingLeaves !== undefined) {
            document.getElementById('cardLeaveCount').textContent = stats.pendingLeaves;
            document.getElementById('cardLeaveSubtext').textContent =
                stats.pendingLeaves === 1 ? 'Pending approval' : `${stats.pendingLeaves} pending`;
        }
    } catch (err) {
        console.error('Error fetching stats:', err);
    }
}

async function fetchAllEmployees() {
    try {
        const res = await fetch(`${API_BASE}/admin/employees`);
        allEmployees = await res.json();
        renderDirectoryTable(allEmployees);
        populateAssigneeDropdown(allEmployees);
    } catch (err) {
        console.error('Error fetching employees:', err);
    }
}

function renderDirectoryTable(employees) {
    const tbody = document.getElementById('directoryTableBody');
    tbody.innerHTML = employees.map(emp => `
        <tr>
            <td>#${emp.id}</td>
            <td><strong>${emp.name}</strong></td>
            <td>${emp.email}</td>
            <td>${emp.department || 'N/A'}</td>
            <td><span class="pill-tag tag-blue">${emp.role}</span></td>
            <td>
                <button class="btn-action-sm btn-reject" onclick="deleteEmployee(${emp.id})"><i class="fa-solid fa-trash"></i> Delete</button>
            </td>
        </tr>
    `).join('');
}

function populateAssigneeDropdown(employees) {
    const select = document.getElementById('modalTaskAssignee');
    select.innerHTML = '<option value="">-- Select Employee --</option>' +
        employees.map(e => `<option value="${e.id}">${e.name} (${e.department || 'Staff'})</option>`).join('');
}

async function handleAddEmployee(e) {
    e.preventDefault();
    const name = document.getElementById('modalEmpName').value.trim();
    const email = document.getElementById('modalEmpEmail').value.trim();
    const password = document.getElementById('modalEmpPassword').value.trim();
    const department = document.getElementById('modalEmpDept').value.trim();
    const role = document.getElementById('modalEmpRole').value;

    try {
        const res = await fetch(`${API_BASE}/admin/employees`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password, department, role })
        });

        if (!res.ok) throw new Error('Failed to add employee');

        closeModal('addEmployeeModal');
        document.getElementById('addEmployeeForm').reset();
        fetchAllEmployees();
    } catch (err) {
        alert(err.message);
    }
}

async function deleteEmployee(id) {
    if (!confirm('Are you sure you want to delete this employee?')) return;
    await fetch(`${API_BASE}/admin/employees/${id}`, { method: 'DELETE' });
    fetchAllEmployees();
}

function renderProfileInfo() {
    document.getElementById('profName').textContent = currentUser.name;
    document.getElementById('profEmail').textContent = currentUser.email;
    document.getElementById('profDept').textContent = currentUser.department || 'General';
    document.getElementById('profRole').textContent = currentUser.role;
    document.getElementById('profAvatar').textContent = currentUser.name.charAt(0).toUpperCase();
}

// --- UTILITIES ---

function openModal(id) { document.getElementById(id).classList.remove('hidden'); }
function closeModal(id) { document.getElementById(id).classList.add('hidden'); }

function showAlert(el, msg, type) {
    el.className = `alert-box alert-${type}`;
    el.textContent = msg;
    el.classList.remove('hidden');
}

function handleGlobalSearch(query) {
    if (!query) {
        renderTasksTable(allTasks);
        return;
    }
    const filtered = allTasks.filter(t => 
        t.title.toLowerCase().includes(query.toLowerCase()) || 
        t.employeeName.toLowerCase().includes(query.toLowerCase())
    );
    renderTasksTable(filtered);
}
