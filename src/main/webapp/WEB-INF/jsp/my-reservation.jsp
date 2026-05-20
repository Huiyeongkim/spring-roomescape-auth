<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="true" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>내 예약 조회 - 방탈출 예약</title>
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
        .list-card { background: #151932; border: 1px solid #1f2547; border-radius: 12px; padding: 32px; }
        .list-card h2 { font-size: 1.25rem; font-weight: 600; color: #ffffff; margin-bottom: 24px; }
        table { width: 100%; border-collapse: collapse; }
        th { background: #1a1f3a; color: #c5cae9; padding: 14px; text-align: left; font-weight: 600; font-size: 0.9rem; border: 1px solid #1f2547; }
        td { padding: 14px; border: 1px solid #1f2547; color: #c5cae9; font-size: 0.9rem; }
        tr:hover { background: #1a1f3a; }
        .btn-delete { background: #f5576c; color: white; padding: 6px 14px; border: none; border-radius: 6px; cursor: pointer; font-weight: 600; font-size: 0.85rem; font-family: 'Noto Sans KR', sans-serif; transition: all 0.2s; }
        .btn-delete:hover { background: #f34359; }
        .empty-state { text-align: center; color: #5c6686; padding: 40px; font-size: 0.95rem; }
        .alert { padding: 12px 16px; border-radius: 8px; margin-bottom: 16px; font-size: 0.9rem; }
        .alert-error { background: #2d1a1f; border: 1px solid #f5576c; color: #f5576c; }
    </style>
</head>
<body>
    <div class="container">
        <nav class="nav"><a href="/" class="back-btn">← 메인으로</a></nav>
        <header><h1>내 예약 조회</h1></header>

        <div id="authAlert"></div>

        <div class="list-card">
            <h2>내 예약 내역</h2>
            <div id="reservationsList"><div class="empty-state">로딩 중...</div></div>
        </div>
    </div>

    <script>
        function getToken() { return localStorage.getItem('accessToken'); }
        function authHeader() { return getToken() ? { 'Authorization': 'Bearer ' + getToken() } : {}; }

        document.addEventListener('DOMContentLoaded', function() {
            if (!getToken()) {
                document.getElementById('authAlert').innerHTML = '<div class="alert alert-error">로그인이 필요합니다. <a href="/" style="color:#f5576c">메인으로 이동</a></div>';
                document.getElementById('reservationsList').innerHTML = '<div class="empty-state">로그인 후 이용해주세요.</div>';
                return;
            }
            loadReservations();
        });

        function loadReservations() {
            fetch('/reservations/mine', { headers: authHeader() })
                .then(r => r.json())
                .then(result => {
                    if (!result.ok) {
                        document.getElementById('reservationsList').innerHTML = '<div class="empty-state">' + result.message + '</div>';
                        return;
                    }
                    const reservations = result.data;
                    if (reservations.length === 0) {
                        document.getElementById('reservationsList').innerHTML = '<div class="empty-state">예약 내역이 없습니다.</div>';
                        return;
                    }
                    document.getElementById('reservationsList').innerHTML = `
                        <table>
                            <thead><tr><th>날짜</th><th>시간</th><th>테마</th><th>관리</th></tr></thead>
                            <tbody>
                                ${reservations.map(r => `
                                    <tr>
                                        <td>${r.date}</td>
                                        <td>${r.time.startAt}</td>
                                        <td>${r.theme.name}</td>
                                        <td><button class="btn-delete" onclick="deleteReservation(${r.id})">취소</button></td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>`;
                })
                .catch(() => {
                    document.getElementById('reservationsList').innerHTML = '<div class="empty-state">예약 목록을 불러오는데 실패했습니다.</div>';
                });
        }

        function deleteReservation(id) {
            if (!confirm('정말로 이 예약을 취소하시겠습니까?')) return;
            fetch('/reservations/' + id, { method: 'DELETE', headers: authHeader() })
                .then(r => r.json())
                .then(result => {
                    if (result.ok) { alert('예약이 취소되었습니다.'); loadReservations(); }
                    else alert('취소 실패: ' + result.message);
                });
        }
    </script>
</body>
</html>
