<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="true" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>예약 - 방탈출 예약</title>
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
        .form-group input, .form-group select { width: 100%; padding: 12px 16px; background: #0a0e27; border: 1px solid #1f2547; border-radius: 8px; font-size: 0.95rem; color: #ffffff; font-family: 'Noto Sans KR', sans-serif; transition: all 0.2s; }
        .form-group input:focus, .form-group select:focus { outline: none; border-color: #667eea; }
        .form-group select option { background: #151932; }
        .time-picker { background: #10152d; border: 1px solid #1f2547; border-radius: 12px; padding: 18px; }
        .time-picker-guide { color: #8b93b0; font-size: 0.85rem; margin-bottom: 16px; }
        .time-slots { display: grid; gap: 14px; }
        .time-slot-group-title { color: #ffffff; font-size: 0.95rem; font-weight: 600; margin-bottom: 10px; }
        .time-slot-group-buttons { display: grid; grid-template-columns: repeat(auto-fit, minmax(110px, 1fr)); gap: 10px; }
        .time-slot-btn { width: 100%; padding: 14px 12px; border-radius: 10px; border: 1px solid #2a315b; background: #151932; color: #c5cae9; font-size: 0.95rem; font-weight: 600; font-family: 'Noto Sans KR', sans-serif; cursor: pointer; transition: all 0.2s; }
        .time-slot-btn:hover { border-color: #667eea; color: #ffffff; }
        .time-slot-btn.selected { background: linear-gradient(135deg, #667eea, #764ba2); border-color: transparent; color: #ffffff; }
        .time-slot-empty { padding: 18px; border-radius: 10px; background: #151932; color: #5c6686; text-align: center; font-size: 0.9rem; border: 1px dashed #2a315b; }
        .btn { background: linear-gradient(135deg, #667eea, #764ba2); color: white; padding: 12px 24px; border: none; border-radius: 8px; font-size: 0.95rem; font-weight: 600; cursor: pointer; font-family: 'Noto Sans KR', sans-serif; transition: all 0.2s; }
        .btn:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3); }
        .alert { padding: 12px 16px; border-radius: 8px; margin-bottom: 16px; font-size: 0.9rem; }
        .alert-error { background: #2d1a1f; border: 1px solid #f5576c; color: #f5576c; }
        .alert-info { background: #1a1f3a; border: 1px solid #667eea; color: #667eea; }
    </style>
</head>
<body>
    <div class="container">
        <nav class="nav">
            <a href="/" class="back-btn">← 메인으로</a>
        </nav>
        <header><h1>예약하기</h1></header>

        <div id="authAlert"></div>

        <div class="form-card">
            <h2>새 예약 만들기</h2>
            <form id="reservationForm">
                <div class="form-group">
                    <label for="date">날짜</label>
                    <input type="date" id="date" name="date" required>
                </div>
                <div class="form-group">
                    <label>시간</label>
                    <div class="time-picker">
                        <p class="time-picker-guide">원하는 시간대를 선택하세요.</p>
                        <div id="timeSlots" class="time-slots">
                            <div class="time-slot-empty">날짜와 테마를 먼저 선택하세요.</div>
                        </div>
                        <input type="hidden" id="timeId" name="timeId" required>
                    </div>
                </div>
                <div class="form-group">
                    <label for="themeId">테마</label>
                    <select id="themeId" name="themeId" required onchange="loadAvailableTimes()">
                        <option value="">테마를 선택하세요</option>
                    </select>
                </div>
                <button type="submit" class="btn">예약하기</button>
            </form>
        </div>
    </div>

    <script>
        function getToken() { return localStorage.getItem('accessToken'); }

        function authHeader() {
            const token = getToken();
            return token ? { 'Authorization': 'Bearer ' + token } : {};
        }

        document.addEventListener('DOMContentLoaded', function() {
            if (!getToken()) {
                document.getElementById('authAlert').innerHTML = '<div class="alert alert-error">로그인이 필요합니다. <a href="/" style="color:#f5576c">메인으로 이동</a></div>';
            }
            loadThemes();
        });

        document.getElementById('date').addEventListener('change', loadAvailableTimes);

        function loadAvailableTimes() {
            const date = document.getElementById('date').value;
            const themeId = document.getElementById('themeId').value;

            if (!date || !themeId) {
                document.getElementById('timeSlots').innerHTML = '<div class="time-slot-empty">날짜와 테마를 먼저 선택하세요.</div>';
                return;
            }

            fetch(`/times?themeId=${themeId}&date=${date}`)
                .then(r => r.json())
                .then(result => renderTimeSlots(result.data));
        }

        function renderTimeSlots(times) {
            const container = document.getElementById('timeSlots');
            if (!times || times.length === 0) {
                container.innerHTML = '<div class="time-slot-empty">선택 가능한 시간이 없습니다.</div>';
                return;
            }
            const groups = { '오전': [], '오후': [], '저녁': [] };
            times.forEach(t => {
                const h = parseInt(t.startAt.split(':')[0]);
                if (h < 12) groups['오전'].push(t);
                else if (h < 18) groups['오후'].push(t);
                else groups['저녁'].push(t);
            });
            container.innerHTML = Object.entries(groups)
                .filter(([, g]) => g.length > 0)
                .map(([label, g]) => `
                    <div>
                        <p class="time-slot-group-title">${label}</p>
                        <div class="time-slot-group-buttons">
                            ${g.map(t => `<button type="button" class="time-slot-btn" data-time-id="${t.id}" onclick="selectTime(${t.id})">${t.startAt}</button>`).join('')}
                        </div>
                    </div>
                `).join('');
        }

        function selectTime(timeId) {
            document.getElementById('timeId').value = timeId;
            document.querySelectorAll('.time-slot-btn').forEach(btn => btn.classList.toggle('selected', parseInt(btn.dataset.timeId) === timeId));
        }

        function loadThemes() {
            fetch('/themes')
                .then(r => r.json())
                .then(result => {
                    const select = document.getElementById('themeId');
                    result.data.forEach(t => {
                        const opt = document.createElement('option');
                        opt.value = t.id;
                        opt.textContent = t.name;
                        select.appendChild(opt);
                    });
                });
        }

        document.getElementById('reservationForm').addEventListener('submit', function(e) {
            e.preventDefault();
            if (!getToken()) { alert('로그인이 필요합니다.'); return; }
            if (!document.getElementById('timeId').value) { alert('시간을 선택해주세요.'); return; }

            fetch('/reservations', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', ...authHeader() },
                body: JSON.stringify({
                    date: document.getElementById('date').value,
                    timeId: parseInt(document.getElementById('timeId').value),
                    themeId: parseInt(document.getElementById('themeId').value)
                })
            })
            .then(r => r.json())
            .then(result => {
                if (result.ok) {
                    alert('예약이 완료되었습니다!');
                    this.reset();
                    document.querySelectorAll('.time-slot-btn').forEach(b => b.classList.remove('selected'));
                    document.getElementById('timeSlots').innerHTML = '<div class="time-slot-empty">날짜와 테마를 먼저 선택하세요.</div>';
                } else {
                    alert('예약 실패: ' + result.message);
                }
            });
        });
    </script>
</body>
</html>
