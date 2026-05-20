<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="true" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>테마 관리 - 방탈출 예약</title>
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700&display=swap" rel="stylesheet">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Noto Sans KR', sans-serif; background: #0a0e27; min-height: 100vh; padding: 40px 20px; }
        .container { max-width: 1200px; margin: 0 auto; }
        .nav { margin-bottom: 40px; }
        .back-btn { display: inline-flex; align-items: center; gap: 8px; padding: 10px 20px; background: #151932; color: #c5cae9; text-decoration: none; border-radius: 8px; font-size: 0.9rem; font-weight: 500; border: 1px solid #1f2547; transition: all 0.2s; }
        .back-btn:hover { background: #1a1f3a; border-color: #2d3561; color: #ffffff; }
        header { text-align: center; margin-bottom: 60px; }
        h1 { font-size: 2.5rem; font-weight: 700; color: #ffffff; margin-bottom: 12px; }
        .form-card { background: #151932; border: 1px solid #1f2547; border-radius: 12px; padding: 32px; margin-bottom: 40px; }
        .form-card h2 { font-size: 1.25rem; font-weight: 600; color: #ffffff; margin-bottom: 24px; }
        .form-group { margin-bottom: 20px; }
        .form-group label { display: block; margin-bottom: 8px; color: #c5cae9; font-size: 0.9rem; font-weight: 500; }
        .form-group input { width: 100%; padding: 12px 16px; background: #0a0e27; border: 1px solid #1f2547; border-radius: 8px; font-size: 0.95rem; color: #ffffff; font-family: 'Noto Sans KR', sans-serif; }
        .form-group input:focus { outline: none; border-color: #667eea; }
        .form-group input::placeholder { color: #5c6686; }
        .btn { background: linear-gradient(135deg, #667eea, #764ba2); color: white; padding: 12px 24px; border: none; border-radius: 8px; font-size: 0.95rem; font-weight: 600; cursor: pointer; font-family: 'Noto Sans KR', sans-serif; transition: all 0.2s; }
        .btn:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3); }
        .list-card { background: #151932; border: 1px solid #1f2547; border-radius: 12px; padding: 32px; }
        .list-card h2 { font-size: 1.25rem; font-weight: 600; color: #ffffff; margin-bottom: 24px; }
        .theme-item { background: #1a1f3a; padding: 20px; margin-bottom: 12px; border-radius: 10px; border: 1px solid #1f2547; display: flex; justify-content: space-between; align-items: center; transition: all 0.2s; }
        .theme-item:hover { border-color: #2d3561; }
        .theme-name { font-size: 1.1rem; font-weight: 600; color: #ffffff; margin-bottom: 8px; }
        .theme-description { font-size: 0.9rem; color: #8b93b0; margin-bottom: 6px; }
        .theme-url { font-size: 0.85rem; color: #667eea; text-decoration: none; }
        .theme-url:hover { text-decoration: underline; }
        .btn-delete { background: #f5576c; color: white; padding: 8px 16px; border: none; border-radius: 6px; cursor: pointer; font-weight: 600; font-size: 0.85rem; font-family: 'Noto Sans KR', sans-serif; transition: all 0.2s; }
        .btn-delete:hover { background: #f34359; }
        .empty-state { text-align: center; color: #5c6686; padding: 40px; font-size: 0.95rem; }
        .alert { padding: 12px 16px; border-radius: 8px; margin-bottom: 16px; font-size: 0.9rem; }
        .alert-error { background: #2d1a1f; border: 1px solid #f5576c; color: #f5576c; }
    </style>
</head>
<body>
    <div class="container">
        <nav class="nav"><a href="/" class="back-btn">← 메인으로</a></nav>
        <header><h1>테마 관리</h1></header>

        <div id="authAlert"></div>

        <div class="form-card">
            <h2>새 테마 추가</h2>
            <form id="themeForm">
                <div class="form-group">
                    <label>테마 이름</label>
                    <input type="text" id="name" required placeholder="예: 범인은 바로 너!">
                </div>
                <div class="form-group">
                    <label>설명</label>
                    <input type="text" id="description" placeholder="테마 설명을 입력하세요">
                </div>
                <div class="form-group">
                    <label>썸네일 URL</label>
                    <input type="text" id="url" placeholder="https://example.com/image.jpg">
                </div>
                <button type="submit" class="btn">테마 추가</button>
            </form>
        </div>

        <div class="list-card">
            <h2>테마 목록</h2>
            <div id="themesList"><div class="empty-state">로딩 중...</div></div>
        </div>
    </div>

    <script>
        function getToken() { return localStorage.getItem('accessToken'); }
        function authHeader() { return getToken() ? { 'Authorization': 'Bearer ' + getToken() } : {}; }

        document.addEventListener('DOMContentLoaded', function() {
            if (!getToken()) {
                document.getElementById('authAlert').innerHTML = '<div class="alert alert-error">로그인이 필요합니다. <a href="/" style="color:#f5576c">메인으로 이동</a></div>';
            }
            loadThemes();
        });

        function loadThemes() {
            fetch('/themes')
                .then(r => r.json())
                .then(result => {
                    const themes = result.data;
                    if (themes.length === 0) {
                        document.getElementById('themesList').innerHTML = '<div class="empty-state">등록된 테마가 없습니다.</div>';
                        return;
                    }
                    document.getElementById('themesList').innerHTML = themes.map(t => `
                        <div class="theme-item">
                            <div>
                                <div class="theme-name">${t.name}</div>
                                <div class="theme-description">${t.description || ''}</div>
                                <a href="${t.url}" target="_blank" class="theme-url">${t.url}</a>
                            </div>
                            <button class="btn-delete" onclick="deleteTheme(${t.id})">삭제</button>
                        </div>
                    `).join('');
                });
        }

        document.getElementById('themeForm').addEventListener('submit', function(e) {
            e.preventDefault();
            if (!getToken()) { alert('로그인이 필요합니다.'); return; }
            fetch('/admin/themes', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', ...authHeader() },
                body: JSON.stringify({ name: document.getElementById('name').value, description: document.getElementById('description').value, url: document.getElementById('url').value })
            })
            .then(r => r.json())
            .then(result => {
                if (result.ok) { alert('테마가 추가되었습니다!'); this.reset(); loadThemes(); }
                else alert('추가 실패: ' + result.message);
            });
        });

        function deleteTheme(id) {
            if (!confirm('정말로 이 테마를 삭제하시겠습니까?')) return;
            fetch('/admin/themes/' + id, { method: 'DELETE', headers: authHeader() })
                .then(r => r.json())
                .then(result => {
                    if (result.ok) { alert('테마가 삭제되었습니다.'); loadThemes(); }
                    else alert('삭제 실패: ' + result.message);
                });
        }
    </script>
</body>
</html>
