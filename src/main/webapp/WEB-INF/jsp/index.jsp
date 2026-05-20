<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="true" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>방탈출 예약 시스템</title>
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700&display=swap" rel="stylesheet">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Noto Sans KR', sans-serif; background: #0a0e27; min-height: 100vh; display: flex; align-items: center; justify-content: center; padding: 40px 20px; }
        .container { max-width: 1100px; width: 100%; }
        header { text-align: center; margin-bottom: 60px; }
        h1 { font-size: 2.5rem; font-weight: 700; color: #ffffff; margin-bottom: 12px; }
        .subtitle { font-size: 1rem; color: #8b93b0; font-weight: 300; }
        .auth-bar { display: flex; justify-content: flex-end; align-items: center; gap: 12px; margin-bottom: 24px; }
        .auth-info { color: #c5cae9; font-size: 0.9rem; }
        .btn-auth { padding: 8px 20px; border-radius: 8px; font-size: 0.9rem; font-weight: 600; cursor: pointer; font-family: 'Noto Sans KR', sans-serif; border: none; transition: all 0.2s; }
        .btn-login { background: linear-gradient(135deg, #667eea, #764ba2); color: white; }
        .btn-logout { background: #1a1f3a; color: #c5cae9; border: 1px solid #1f2547; }
        .btn-logout:hover { background: #1f2547; }
        .grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 32px; margin-bottom: 40px; }
        .card { background: #151932; border-radius: 16px; padding: 40px; border: 1px solid #1f2547; transition: all 0.3s; }
        .card:hover { border-color: #2d3561; transform: translateY(-4px); }
        .card-header { display: flex; align-items: center; gap: 12px; margin-bottom: 28px; padding-bottom: 20px; border-bottom: 1px solid #1f2547; }
        .card-icon { width: 48px; height: 48px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 24px; }
        .user-card .card-icon { background: linear-gradient(135deg, #667eea, #764ba2); }
        .admin-card .card-icon { background: linear-gradient(135deg, #f093fb, #f5576c); }
        .card-title { font-size: 1.25rem; font-weight: 600; color: #ffffff; }
        .menu-list { list-style: none; display: flex; flex-direction: column; gap: 12px; }
        .menu-link { display: flex; align-items: center; padding: 16px 20px; background: #1a1f3a; color: #c5cae9; text-decoration: none; border-radius: 10px; font-size: 0.95rem; font-weight: 500; transition: all 0.2s; border: 1px solid transparent; }
        .menu-link:hover { background: #1f2547; color: #ffffff; border-color: #2d3561; transform: translateX(4px); }
        footer { text-align: center; padding-top: 40px; border-top: 1px solid #1f2547; }
        .footer-text { color: #5c6686; font-size: 0.875rem; }
        .modal-overlay { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.7); z-index: 100; justify-content: center; align-items: center; }
        .modal-overlay.active { display: flex; }
        .modal { background: #151932; border: 1px solid #1f2547; border-radius: 12px; padding: 32px; width: 100%; max-width: 400px; }
        .modal h2 { font-size: 1.25rem; font-weight: 600; color: #ffffff; margin-bottom: 24px; }
        .form-group { margin-bottom: 20px; }
        .form-group label { display: block; margin-bottom: 8px; color: #c5cae9; font-size: 0.9rem; font-weight: 500; }
        .form-group input { width: 100%; padding: 12px 16px; background: #0a0e27; border: 1px solid #1f2547; border-radius: 8px; font-size: 0.95rem; color: #ffffff; font-family: 'Noto Sans KR', sans-serif; }
        .form-group input:focus { outline: none; border-color: #667eea; }
        .btn-submit { width: 100%; background: linear-gradient(135deg, #667eea, #764ba2); color: white; padding: 12px; border: none; border-radius: 8px; font-size: 0.95rem; font-weight: 600; cursor: pointer; font-family: 'Noto Sans KR', sans-serif; margin-top: 8px; }
        .btn-submit:hover { opacity: 0.9; }
        .btn-secondary { width: 100%; background: #1a1f3a; color: #c5cae9; padding: 10px; border: 1px solid #1f2547; border-radius: 8px; font-size: 0.9rem; font-weight: 600; cursor: pointer; font-family: 'Noto Sans KR', sans-serif; margin-top: 8px; }
        .btn-secondary:hover { background: #1f2547; }
        .modal-close { float: right; background: none; border: none; color: #8b93b0; font-size: 1.5rem; cursor: pointer; }
        .switch-text { text-align: center; margin-top: 16px; color: #8b93b0; font-size: 0.875rem; }
        .switch-link { color: #667eea; cursor: pointer; text-decoration: underline; background: none; border: none; font-size: 0.875rem; font-family: 'Noto Sans KR', sans-serif; }
        @media (max-width: 768px) { .grid { grid-template-columns: 1fr; } h1 { font-size: 2rem; } .card { padding: 32px; } }
    </style>
</head>
<body>
    <div class="container">
        <div class="auth-bar">
            <span class="auth-info" id="authInfo"></span>
            <button class="btn-auth btn-login" id="signupBtn" onclick="openSignupModal()" style="background:#1a1f3a;border:1px solid #1f2547;color:#c5cae9;">회원가입</button>
            <button class="btn-auth btn-login" id="loginBtn" onclick="openLoginModal()">로그인</button>
            <button class="btn-auth btn-logout" id="logoutBtn" style="display:none" onclick="logout()">로그아웃</button>
        </div>

        <header>
            <h1>방탈출 예약 시스템</h1>
            <p class="subtitle">Room Escape Reservation System</p>
        </header>

        <div class="grid">
            <div class="card user-card">
                <div class="card-header">
                    <div class="card-icon">🎭</div>
                    <h2 class="card-title">사용자</h2>
                </div>
                <ul class="menu-list">
                    <li><a href="/theme" class="menu-link">테마 둘러보기</a></li>
                    <li><a href="/reservation" class="menu-link">예약하기</a></li>
                    <li><a href="/my-reservation" class="menu-link">내 예약 조회</a></li>
                </ul>
            </div>
            <div class="card admin-card">
                <div class="card-header">
                    <div class="card-icon">⚙️</div>
                    <h2 class="card-title">관리자</h2>
                </div>
                <ul class="menu-list">
                    <li><a href="/admin/theme" class="menu-link">테마 관리</a></li>
                    <li><a href="/admin/reservation" class="menu-link">예약 관리</a></li>
                    <li><a href="/admin/time" class="menu-link">시간 관리</a></li>
                </ul>
            </div>
        </div>

        <footer>
            <p class="footer-text">© 2026 Room Escape Reservation System</p>
        </footer>
    </div>

    <div class="modal-overlay" id="loginModal">
        <div class="modal">
            <button class="modal-close" onclick="closeLoginModal()">×</button>
            <h2>로그인</h2>
            <form id="loginForm">
                <div class="form-group">
                    <label>아이디</label>
                    <input type="text" id="loginId" placeholder="아이디를 입력하세요" required>
                </div>
                <div class="form-group">
                    <label>비밀번호</label>
                    <input type="password" id="password" placeholder="비밀번호를 입력하세요" required>
                </div>
                <button type="submit" class="btn-submit">로그인</button>
            </form>
            <p class="switch-text">계정이 없으신가요? <button class="switch-link" onclick="closeLoginModal(); openSignupModal();">회원가입</button></p>
        </div>
    </div>

    <div class="modal-overlay" id="signupModal">
        <div class="modal">
            <button class="modal-close" onclick="closeSignupModal()">×</button>
            <h2>회원가입</h2>
            <form id="signupForm">
                <div class="form-group">
                    <label>아이디</label>
                    <input type="text" id="signupLoginId" placeholder="아이디를 입력하세요" required>
                </div>
                <div class="form-group">
                    <label>비밀번호</label>
                    <input type="password" id="signupPassword" placeholder="비밀번호를 입력하세요" required>
                </div>
                <div class="form-group">
                    <label>이름</label>
                    <input type="text" id="signupName" placeholder="이름을 입력하세요" required>
                </div>
                <button type="submit" class="btn-submit">회원가입</button>
            </form>
            <p class="switch-text">이미 계정이 있으신가요? <button class="switch-link" onclick="closeSignupModal(); openLoginModal();">로그인</button></p>
        </div>
    </div>

    <script>
        function getToken() { return localStorage.getItem('accessToken'); }
        function getMemberName() { return localStorage.getItem('memberName'); }

        function updateAuthUI() {
            const token = getToken();
            if (token) {
                document.getElementById('loginBtn').style.display = 'none';
                document.getElementById('signupBtn').style.display = 'none';
                document.getElementById('logoutBtn').style.display = 'block';
                document.getElementById('authInfo').textContent = '로그인 중';
            } else {
                document.getElementById('loginBtn').style.display = 'block';
                document.getElementById('signupBtn').style.display = 'block';
                document.getElementById('logoutBtn').style.display = 'none';
                document.getElementById('authInfo').textContent = '';
            }
        }

        function openLoginModal() { document.getElementById('loginModal').classList.add('active'); }
        function closeLoginModal() { document.getElementById('loginModal').classList.remove('active'); }
        function openSignupModal() { document.getElementById('signupModal').classList.add('active'); }
        function closeSignupModal() { document.getElementById('signupModal').classList.remove('active'); }

        document.getElementById('loginModal').addEventListener('click', function(e) { if (e.target === this) closeLoginModal(); });
        document.getElementById('signupModal').addEventListener('click', function(e) { if (e.target === this) closeSignupModal(); });

        document.getElementById('signupForm').addEventListener('submit', function(e) {
            e.preventDefault();
            fetch('/members', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    loginId: document.getElementById('signupLoginId').value,
                    password: document.getElementById('signupPassword').value,
                    name: document.getElementById('signupName').value
                })
            })
            .then(r => r.json())
            .then(result => {
                if (result.ok) {
                    alert('회원가입이 완료되었습니다. 로그인해주세요.');
                    this.reset();
                    closeSignupModal();
                    openLoginModal();
                } else {
                    alert('회원가입 실패: ' + result.message);
                }
            });
        });

        document.getElementById('loginForm').addEventListener('submit', function(e) {
            e.preventDefault();
            fetch('/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ loginId: document.getElementById('loginId').value, password: document.getElementById('password').value })
            })
            .then(r => r.json())
            .then(result => {
                if (result.ok) {
                    localStorage.setItem('accessToken', result.data.accessToken);
                    localStorage.setItem('refreshToken', result.data.refreshToken);
                    closeLoginModal();
                    updateAuthUI();
                } else {
                    alert('로그인 실패: ' + result.message);
                }
            });
        });

        function logout() {
            const refreshToken = localStorage.getItem('refreshToken');
            if (refreshToken) {
                fetch('/logout', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ refreshToken }) });
            }
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            localStorage.removeItem('memberName');
            updateAuthUI();
        }

        updateAuthUI();
    </script>
</body>
</html>
