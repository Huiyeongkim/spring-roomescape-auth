INSERT INTO reservation_time (start_at) VALUES ('10:00');
INSERT INTO reservation_time (start_at) VALUES ('11:00');
INSERT INTO reservation_time (start_at) VALUES ('12:00');
INSERT INTO reservation_time (start_at) VALUES ('13:00');


INSERT INTO theme (name, description, url)
VALUES ('인형의 집', '공포 테마의 클래식, 밤마다 살아 움직이는 인형들이 가득한 저택을 탈출하세요.', 'https://example.com/1');

INSERT INTO theme (name, description, url)
VALUES ('해적왕의 보물', '침몰한 해적선 속 숨겨진 황금 보물을 찾아 제한 시간 내에 탈출해야 합니다.', 'https://example.com/2');

INSERT INTO theme (name, description, url)
VALUES ('명탐정의 부재', '사라진 명탐정의 사무실에 남겨진 단서들을 조합해 진범을 밝혀내세요.', 'https://example.com/3');

INSERT INTO theme (name, description, url)
VALUES ('우주정거장: 블랙아웃', '산소가 고갈되기 직전의 우주정거장에서 탈출 포드를 가동하세요.', 'https://example.com/4');

INSERT INTO theme (name, description, url)
VALUES ('꿈속의 과자집', '꿈속에서 길을 잃은 당신, 달콤하지만 위험한 과자집의 비밀을 풀어야 합니다.', 'https://example.com/5');


INSERT INTO member(login_id, password, name, role)
VALUES ('id1', 'pass1', '브라운', 'USER');

INSERT INTO member(login_id, password, name, role)
VALUES ('id2', 'pass2', '류시', 'USER');

INSERT INTO member(login_id, password, name, role)
VALUES ('id3', 'pass3', '검프', 'USER');


INSERT INTO reservation (date, member_id, theme_id, time_id)
VALUES ( '2026-05-01',1, 1, 1);

INSERT INTO reservation (date, member_id, theme_id, time_id)
VALUES ('2026-05-01', 2, 2, 3);

INSERT INTO reservation (date, member_id, theme_id, time_id)
VALUES ('2026-05-04', 3,3, 2);

INSERT INTO reservation (date, member_id, theme_id, time_id)
VALUES ('2026-05-03', 1, 5, 2);

INSERT INTO reservation (date, member_id, theme_id, time_id)
VALUES ('2026-05-05', 2, 3, 4);

INSERT INTO reservation (date, member_id, theme_id, time_id)
VALUES ('2026-05-04', 3, 5, 1);
