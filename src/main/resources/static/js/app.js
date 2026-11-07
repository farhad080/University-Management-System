const app = {
  currentUser: null,
  activeTab: 'teachers',

  init() {
    this.checkSession();
    this.bindEvents();
  },

  bindEvents() {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
      loginForm.addEventListener('submit', (e) => {
        e.preventDefault();
        this.login();
      });
    }

    const logoutBtn = document.getElementById('btnLogout');
    if (logoutBtn) {
      logoutBtn.addEventListener('click', () => this.logout());
    }
  },

  quickFill(username, password) {
    document.getElementById('loginUsername').value = username;
    document.getElementById('loginPassword').value = password;
  },

  async checkSession() {
    try {
      const res = await fetch('/api/auth/me');
      if (res.ok) {
        this.currentUser = await res.json();
        this.renderAuthenticatedUI();
      } else {
        this.renderUnauthenticatedUI();
      }
    } catch (err) {
      this.renderUnauthenticatedUI();
    }
  },

  async login() {
    const username = document.getElementById('loginUsername').value.trim();
    const password = document.getElementById('loginPassword').value;
    const btn = document.getElementById('btnLogin');

    btn.disabled = true;
    btn.textContent = 'Signing In...';

    try {
      const res = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
      });

      if (res.ok) {
        const data = await res.json();
        this.currentUser = data;
        this.showToast(`Welcome back, ${data.fullName}!`, 'success');
        this.renderAuthenticatedUI();
      } else {
        const err = await res.json().catch(() => ({ message: 'Invalid username or password' }));
        this.showToast(err.message || 'Login failed', 'error');
      }
    } catch (err) {
      this.showToast('Network error while signing in', 'error');
    } finally {
      btn.disabled = false;
      btn.textContent = 'Sign In to Dashboard';
    }
  },

  async logout() {
    try {
      await fetch('/api/auth/logout', { method: 'POST' });
    } catch (e) {
      // ignore
    }
    this.currentUser = null;
    this.showToast('Successfully signed out', 'success');
    this.renderUnauthenticatedUI();
  },

  renderUnauthenticatedUI() {
    document.getElementById('authSection').style.display = 'block';
    document.getElementById('dashboardSection').style.display = 'none';
    document.getElementById('navActions').style.display = 'none';
  },

  renderAuthenticatedUI() {
    document.getElementById('authSection').style.display = 'none';
    document.getElementById('dashboardSection').style.display = 'block';
    document.getElementById('navActions').style.display = 'flex';

    document.getElementById('navUsername').textContent = this.currentUser.fullName || this.currentUser.username;
    const badge = document.getElementById('navRoleBadge');
    badge.textContent = this.currentUser.role;
    badge.className = 'badge';

    const banner = document.getElementById('roleBanner');
    const bannerText = document.getElementById('roleBannerText');
    banner.className = 'role-banner';

    // Role-Based UI Visibility
    const tabAdmins = document.getElementById('tabBtnAdmins');
    const tabTeachers = document.getElementById('tabBtnTeachers');
    const tabStudents = document.getElementById('tabBtnStudents');

    if (this.currentUser.role === 'ROLE_ADMIN') {
      badge.classList.add('badge-admin');
      banner.classList.add('banner-admin');
      bannerText.textContent = '🛡️ Logged in as Administrator: Full CRUD permissions for Admins, Teachers, and Students.';
      tabAdmins.style.display = 'inline-flex';
      tabTeachers.style.display = 'inline-flex';
      tabStudents.style.display = 'inline-flex';
      this.switchTab('teachers');
    } else if (this.currentUser.role === 'ROLE_TEACHER') {
      badge.classList.add('badge-teacher');
      banner.classList.add('banner-teacher');
      bannerText.textContent = '👨‍🏫 Logged in as Teacher: Permitted to manage Teachers only. Admins & Students access restricted.';
      tabAdmins.style.display = 'none';
      tabStudents.style.display = 'none';
      tabTeachers.style.display = 'inline-flex';
      this.switchTab('teachers');
    } else if (this.currentUser.role === 'ROLE_STUDENT') {
      badge.classList.add('badge-student');
      banner.classList.add('banner-student');
      bannerText.textContent = '🎓 Logged in as Student: Permitted to manage Students only. Admins & Teachers access restricted.';
      tabAdmins.style.display = 'none';
      tabTeachers.style.display = 'none';
      tabStudents.style.display = 'inline-flex';
      this.switchTab('students');
    }
  },

  switchTab(tabName) {
    this.activeTab = tabName;
    ['teachers', 'students', 'admins', 'tester'].forEach(tab => {
      const btn = document.getElementById('tabBtn' + tab.charAt(0).toUpperCase() + tab.slice(1));
      const content = document.getElementById('tabContent' + tab.charAt(0).toUpperCase() + tab.slice(1));
      if (btn) btn.classList.toggle('active', tab === tabName);
      if (content) content.style.display = (tab === tabName) ? 'block' : 'none';
    });

    if (tabName === 'teachers') this.loadTeachers();
    else if (tabName === 'students') this.loadStudents();
    else if (tabName === 'admins') this.loadAdmins();
  },

  // ----------------------------------------------------
  // Teachers CRUD
  // ----------------------------------------------------
  async loadTeachers() {
    const tbody = document.getElementById('teachersTableBody');
    tbody.innerHTML = '<tr><td colspan="8" style="text-align: center; color: var(--text-muted);">Loading teachers...</td></tr>';
    try {
      const res = await fetch('/api/teachers');
      if (res.status === 403) {
        tbody.innerHTML = '<tr><td colspan="8" style="text-align: center; color: var(--accent-danger);">403 Forbidden: You do not have permission to view Teachers.</td></tr>';
        return;
      }
      const teachers = await res.json();
      if (!teachers.length) {
        tbody.innerHTML = '<tr><td colspan="8" style="text-align: center; color: var(--text-muted);">No teachers found.</td></tr>';
        return;
      }
      tbody.innerHTML = teachers.map(t => `
        <tr>
          <td><strong>#${t.id}</strong></td>
          <td>${t.username}</td>
          <td>${t.fullName}</td>
          <td>${t.email}</td>
          <td>${t.department}</td>
          <td><span class="badge badge-teacher">${t.designation}</span></td>
          <td>${t.qualification || '-'}</td>
          <td class="table-actions">
            <button class="btn btn-secondary btn-sm" onclick="app.editTeacher(${t.id})">Edit</button>
            <button class="btn btn-danger btn-sm" onclick="app.deleteTeacher(${t.id})">Delete</button>
          </td>
        </tr>
      `).join('');
    } catch (err) {
      tbody.innerHTML = '<tr><td colspan="8" style="text-align: center; color: var(--accent-danger);">Failed to load teachers.</td></tr>';
    }
  },

  async editTeacher(id) {
    try {
      const res = await fetch(`/api/teachers/${id}`);
      if (!res.ok) throw new Error();
      const t = await res.json();
      document.getElementById('teacherId').value = t.id;
      document.getElementById('teacherUsername').value = t.username;
      document.getElementById('teacherPassword').value = '';
      document.getElementById('teacherFullName').value = t.fullName;
      document.getElementById('teacherEmail').value = t.email;
      document.getElementById('teacherDepartment').value = t.department;
      document.getElementById('teacherDesignation').value = t.designation;
      document.getElementById('teacherQualification').value = t.qualification || '';
      document.getElementById('teacherModalTitle').textContent = `Edit Teacher: ${t.fullName}`;
      this.openModal('teacherModal');
    } catch (err) {
      this.showToast('Could not load teacher details', 'error');
    }
  },

  async saveTeacher(e) {
    e.preventDefault();
    const id = document.getElementById('teacherId').value;
    const body = {
      username: document.getElementById('teacherUsername').value.trim(),
      password: document.getElementById('teacherPassword').value,
      fullName: document.getElementById('teacherFullName').value.trim(),
      email: document.getElementById('teacherEmail').value.trim(),
      department: document.getElementById('teacherDepartment').value.trim(),
      designation: document.getElementById('teacherDesignation').value.trim(),
      qualification: document.getElementById('teacherQualification').value.trim()
    };

    if (!id && !body.password) {
      this.showToast('Password is required for new teacher', 'error');
      return;
    }

    const url = id ? `/api/teachers/${id}` : '/api/teachers';
    const method = id ? 'PUT' : 'POST';

    try {
      const res = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      });

      if (res.ok) {
        this.showToast(`Teacher ${id ? 'updated' : 'created'} successfully!`, 'success');
        this.closeModal('teacherModal');
        this.loadTeachers();
      } else {
        const err = await res.json().catch(() => ({ message: 'Save failed' }));
        this.showToast(err.message || 'Error saving teacher', 'error');
      }
    } catch (err) {
      this.showToast('Network error while saving teacher', 'error');
    }
  },

  async deleteTeacher(id) {
    if (!confirm('Are you sure you want to delete this teacher?')) return;
    try {
      const res = await fetch(`/api/teachers/${id}`, { method: 'DELETE' });
      if (res.ok) {
        this.showToast('Teacher deleted successfully', 'success');
        this.loadTeachers();
      } else {
        const err = await res.json().catch(() => ({ message: 'Deletion failed' }));
        this.showToast(err.message || 'Could not delete teacher', 'error');
      }
    } catch (err) {
      this.showToast('Error deleting teacher', 'error');
    }
  },

  // ----------------------------------------------------
  // Students CRUD
  // ----------------------------------------------------
  async loadStudents() {
    const tbody = document.getElementById('studentsTableBody');
    tbody.innerHTML = '<tr><td colspan="9" style="text-align: center; color: var(--text-muted);">Loading students...</td></tr>';
    try {
      const res = await fetch('/api/students');
      if (res.status === 403) {
        tbody.innerHTML = '<tr><td colspan="9" style="text-align: center; color: var(--accent-danger);">403 Forbidden: You do not have permission to view Students.</td></tr>';
        return;
      }
      const students = await res.json();
      if (!students.length) {
        tbody.innerHTML = '<tr><td colspan="9" style="text-align: center; color: var(--text-muted);">No students found.</td></tr>';
        return;
      }
      tbody.innerHTML = students.map(s => `
        <tr>
          <td><strong>#${s.id}</strong></td>
          <td><span class="badge badge-student">${s.studentIdNumber}</span></td>
          <td>${s.username}</td>
          <td>${s.fullName}</td>
          <td>${s.email}</td>
          <td>${s.department}</td>
          <td>Semester ${s.semester || '-'}</td>
          <td><strong>${s.cgpa ? s.cgpa.toFixed(2) : '-'}</strong></td>
          <td class="table-actions">
            <button class="btn btn-secondary btn-sm" onclick="app.editStudent(${s.id})">Edit</button>
            <button class="btn btn-danger btn-sm" onclick="app.deleteStudent(${s.id})">Delete</button>
          </td>
        </tr>
      `).join('');
    } catch (err) {
      tbody.innerHTML = '<tr><td colspan="9" style="text-align: center; color: var(--accent-danger);">Failed to load students.</td></tr>';
    }
  },

  async editStudent(id) {
    try {
      const res = await fetch(`/api/students/${id}`);
      if (!res.ok) throw new Error();
      const s = await res.json();
      document.getElementById('studentId').value = s.id;
      document.getElementById('studentUsername').value = s.username;
      document.getElementById('studentPassword').value = '';
      document.getElementById('studentFullName').value = s.fullName;
      document.getElementById('studentEmail').value = s.email;
      document.getElementById('studentIdNumber').value = s.studentIdNumber;
      document.getElementById('studentDepartment').value = s.department;
      document.getElementById('studentSemester').value = s.semester || 1;
      document.getElementById('studentCgpa').value = s.cgpa || 3.5;
      document.getElementById('studentModalTitle').textContent = `Edit Student: ${s.fullName}`;
      this.openModal('studentModal');
    } catch (err) {
      this.showToast('Could not load student details', 'error');
    }
  },

  async saveStudent(e) {
    e.preventDefault();
    const id = document.getElementById('studentId').value;
    const body = {
      username: document.getElementById('studentUsername').value.trim(),
      password: document.getElementById('studentPassword').value,
      fullName: document.getElementById('studentFullName').value.trim(),
      email: document.getElementById('studentEmail').value.trim(),
      studentIdNumber: document.getElementById('studentIdNumber').value.trim(),
      department: document.getElementById('studentDepartment').value.trim(),
      semester: parseInt(document.getElementById('studentSemester').value, 10),
      cgpa: parseFloat(document.getElementById('studentCgpa').value)
    };

    if (!id && !body.password) {
      this.showToast('Password is required for new student', 'error');
      return;
    }

    const url = id ? `/api/students/${id}` : '/api/students';
    const method = id ? 'PUT' : 'POST';

    try {
      const res = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      });

      if (res.ok) {
        this.showToast(`Student ${id ? 'updated' : 'created'} successfully!`, 'success');
        this.closeModal('studentModal');
        this.loadStudents();
      } else {
        const err = await res.json().catch(() => ({ message: 'Save failed' }));
        this.showToast(err.message || 'Error saving student', 'error');
      }
    } catch (err) {
      this.showToast('Network error while saving student', 'error');
    }
  },

  async deleteStudent(id) {
    if (!confirm('Are you sure you want to delete this student?')) return;
    try {
      const res = await fetch(`/api/students/${id}`, { method: 'DELETE' });
      if (res.ok) {
        this.showToast('Student deleted successfully', 'success');
        this.loadStudents();
      } else {
        const err = await res.json().catch(() => ({ message: 'Deletion failed' }));
        this.showToast(err.message || 'Could not delete student', 'error');
      }
    } catch (err) {
      this.showToast('Error deleting student', 'error');
    }
  },

  // ----------------------------------------------------
  // Admins CRUD
  // ----------------------------------------------------
  async loadAdmins() {
    const tbody = document.getElementById('adminsTableBody');
    tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; color: var(--text-muted);">Loading admins...</td></tr>';
    try {
      const res = await fetch('/api/admins');
      if (res.status === 403) {
        tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; color: var(--accent-danger);">403 Forbidden: You do not have permission to view Admins.</td></tr>';
        return;
      }
      const admins = await res.json();
      if (!admins.length) {
        tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; color: var(--text-muted);">No admins found.</td></tr>';
        return;
      }
      tbody.innerHTML = admins.map(a => `
        <tr>
          <td><strong>#${a.id}</strong></td>
          <td>${a.username}</td>
          <td>${a.fullName}</td>
          <td>${a.email}</td>
          <td>${a.department || '-'}</td>
          <td>${a.officeLocation || '-'}</td>
          <td class="table-actions">
            <button class="btn btn-secondary btn-sm" onclick="app.editAdmin(${a.id})">Edit</button>
            <button class="btn btn-danger btn-sm" onclick="app.deleteAdmin(${a.id})">Delete</button>
          </td>
        </tr>
      `).join('');
    } catch (err) {
      tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; color: var(--accent-danger);">Failed to load admins.</td></tr>';
    }
  },

  async editAdmin(id) {
    try {
      const res = await fetch(`/api/admins/${id}`);
      if (!res.ok) throw new Error();
      const a = await res.json();
      document.getElementById('adminId').value = a.id;
      document.getElementById('adminUsername').value = a.username;
      document.getElementById('adminPassword').value = '';
      document.getElementById('adminFullName').value = a.fullName;
      document.getElementById('adminEmail').value = a.email;
      document.getElementById('adminDepartment').value = a.department || '';
      document.getElementById('adminOfficeLocation').value = a.officeLocation || '';
      document.getElementById('adminModalTitle').textContent = `Edit Admin: ${a.fullName}`;
      this.openModal('adminModal');
    } catch (err) {
      this.showToast('Could not load admin details', 'error');
    }
  },

  async saveAdmin(e) {
    e.preventDefault();
    const id = document.getElementById('adminId').value;
    const body = {
      username: document.getElementById('adminUsername').value.trim(),
      password: document.getElementById('adminPassword').value,
      fullName: document.getElementById('adminFullName').value.trim(),
      email: document.getElementById('adminEmail').value.trim(),
      department: document.getElementById('adminDepartment').value.trim(),
      officeLocation: document.getElementById('adminOfficeLocation').value.trim()
    };

    if (!id && !body.password) {
      this.showToast('Password is required for new admin', 'error');
      return;
    }

    const url = id ? `/api/admins/${id}` : '/api/admins';
    const method = id ? 'PUT' : 'POST';

    try {
      const res = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      });

      if (res.ok) {
        this.showToast(`Admin ${id ? 'updated' : 'created'} successfully!`, 'success');
        this.closeModal('adminModal');
        this.loadAdmins();
      } else {
        const err = await res.json().catch(() => ({ message: 'Save failed' }));
        this.showToast(err.message || 'Error saving admin', 'error');
      }
    } catch (err) {
      this.showToast('Network error while saving admin', 'error');
    }
  },

  async deleteAdmin(id) {
    if (!confirm('Are you sure you want to delete this administrator?')) return;
    try {
      const res = await fetch(`/api/admins/${id}`, { method: 'DELETE' });
      if (res.ok) {
        this.showToast('Admin deleted successfully', 'success');
        this.loadAdmins();
      } else {
        const err = await res.json().catch(() => ({ message: 'Deletion failed' }));
        this.showToast(err.message || 'Could not delete admin', 'error');
      }
    } catch (err) {
      this.showToast('Error deleting admin', 'error');
    }
  },

  // ----------------------------------------------------
  // Live RBAC Tester
  // ----------------------------------------------------
  async testEndpoint(endpoint) {
    const output = document.getElementById('testOutput');
    const badge = document.getElementById('responseStatusBadge');
    output.textContent = `Sending GET request to ${endpoint} with session credentials...`;
    badge.textContent = 'Executing...';
    badge.className = 'badge';

    const startTime = performance.now();
    try {
      const res = await fetch(endpoint);
      const duration = (performance.now() - startTime).toFixed(1);
      const statusText = `${res.status} ${res.statusText}`;

      badge.textContent = statusText;
      if (res.status === 200 || res.status === 201) {
        badge.className = 'badge badge-student';
      } else if (res.status === 403) {
        badge.className = 'badge badge-admin';
      } else {
        badge.className = 'badge';
      }

      const contentType = res.headers.get('content-type') || '';
      let json = {};
      if (contentType.includes('application/json')) {
        json = await res.json();
      } else {
        json = { text: await res.text() };
      }

      output.textContent = `Endpoint: ${endpoint}\nStatus: ${statusText}\nTime: ${duration}ms\n\nResponse Body:\n` + JSON.stringify(json, null, 2);
    } catch (err) {
      badge.textContent = 'Error';
      badge.className = 'badge badge-admin';
      output.textContent = `Request failed: ${err.message}`;
    }
  },

  // ----------------------------------------------------
  // Modal & Toast Helpers
  // ----------------------------------------------------
  openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
      modal.style.display = 'flex';
      // If creating new, clear id
      if (!modal.dataset.editing) {
        const form = modal.querySelector('form');
        if (form && !modal.dataset.editing) {
          // keep values if editing
        }
      }
    }
  },

  closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
      modal.style.display = 'none';
      const form = modal.querySelector('form');
      if (form) form.reset();
      const idInput = modal.querySelector('input[type="hidden"]');
      if (idInput) idInput.value = '';
      const title = modal.querySelector('.modal-title');
      if (title) {
        if (modalId === 'teacherModal') title.textContent = 'Add Teacher';
        if (modalId === 'studentModal') title.textContent = 'Add Student';
        if (modalId === 'adminModal') title.textContent = 'Add Administrator';
      }
    }
  },

  showToast(message, type = 'success') {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `<span>${type === 'success' ? '✓' : '⚠️'}</span> <span>${message}</span>`;
    container.appendChild(toast);
    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transition = 'opacity 0.3s ease';
      setTimeout(() => toast.remove(), 300);
    }, 3500);
  }
};

window.addEventListener('DOMContentLoaded', () => app.init());
