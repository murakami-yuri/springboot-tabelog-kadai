/* rolesテーブル */
INSERT IGNORE INTO roles (id, name) VALUES (1, 'ROLE_GENERAL');
INSERT IGNORE INTO roles (id, name) VALUES (2, 'ROLE_ADMIN');

/* usersテーブル */
INSERT IGNORE INTO users (id, role_id, email, password, nickname, enabled, eligible) VALUES (1, 1, 'taro.samurai@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', 'taro', true, false); -- GENERAL
INSERT IGNORE INTO users (id, role_id, email, password, nickname, enabled, eligible) VALUES (2, 2, 'hanako.samurai@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', 'hanako', true, false); -- ADMIN
INSERT IGNORE INTO users (id, role_id, email, password, nickname, enabled, eligible) VALUES (3, 1, 'ichiro.samurai@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', 'ichiro', true, true); -- ELIGIBLE
INSERT IGNORE INTO users (id, role_id, email, password, nickname, enabled, eligible) VALUES (4, 1, 'jiro.samurai@example.com', 'password', 'jiro', false, false);
INSERT IGNORE INTO users (id, role_id, email, password, nickname, enabled, eligible) VALUES (5, 1, 'saburo.samurai@example.com', 'password', 'saburo', false, false);
INSERT IGNORE INTO users (id, role_id, email, password, nickname, enabled, eligible) VALUES (6, 1, 'shiro.samurai@example.com', 'password', 'shiro', false, false);

 /* shopsテーブル */
 INSERT IGNORE INTO shops (id, name, postal_code, address, phone_number, description, image_name) VALUES (1, '牡羊屋', '453-0000', '愛知県名古屋市１−１', '111-111-1111', '地元の人に愛される昔ながらのラーメン屋さんです。', 'shop01.jpg' );
 INSERT IGNORE INTO shops (id, name, postal_code, address, phone_number, description, image_name) VALUES (2, 'おうし屋', '453-0000', '愛知県名古屋市１−１', '111-111-1111', '地元の人に愛される昔ながらの焼き鳥屋さんです。', 'shop02.jpg' );
 INSERT IGNORE INTO shops (id, name, postal_code, address, phone_number, description, image_name) VALUES (3, 'Futago屋', '453-0000', '愛知県名古屋市１−１', '111-111-1111', '地元の人に愛される昔ながらの鰻屋さんです。', 'shop03.jpg' );
 INSERT IGNORE INTO shops (id, name, postal_code, address, phone_number, description, image_name) VALUES (4, 'かに屋', '453-0000', '愛知県名古屋市１−１', '111-111-1111', '地元の人に愛される昔ながらのグリル屋さんです。', 'shop04.jpg' );
 INSERT IGNORE INTO shops (id, name, postal_code, address, phone_number, description, image_name) VALUES (5, '獅子屋', '453-0000', '愛知県名古屋市１−１', '111-111-1111', '地元の人に愛される昔ながらのエスニック料理屋さんです。', 'shop05.jpg' );
 INSERT IGNORE INTO shops (id, name, postal_code, address, phone_number, description, image_name) VALUES (6, '乙女屋', '453-0000', '愛知県名古屋市１−１', '111-111-1111', '地元の人に愛される昔ながらのスイーツ屋さんです。', 'shop06.jpg' );
 INSERT IGNORE INTO shops (id, name, postal_code, address, phone_number, description, image_name) VALUES (7, 'てんびん屋', '453-0000', '愛知県名古屋市１−１', '111-111-1111', '地元の人に愛される昔ながらの洋食屋さんです。', 'shop07.jpg' );
 INSERT IGNORE INTO shops (id, name, postal_code, address, phone_number, description, image_name) VALUES (8, 'Sasori屋', '453-0000', '愛知県名古屋市１−１', '111-111-1111', '地元の人に愛される昔ながらのラーメン屋さんです。', 'shop08.jpg' );
 INSERT IGNORE INTO shops (id, name, postal_code, address, phone_number, description, image_name) VALUES (9, '射手屋', '453-0000', '愛知県名古屋市１−１', '111-111-1111', '地元の人に愛される昔ながらのラーメン屋さんです。', 'shop09.jpg' );
 INSERT IGNORE INTO shops (id, name, postal_code, address, phone_number, description, image_name) VALUES (10, 'やぎ屋', '453-0000', '愛知県名古屋市１−１', '111-111-1111', '地元の人に愛される昔ながらの飲み屋さんです。', 'shop10.jpg' );
 INSERT IGNORE INTO shops (id, name, postal_code, address, phone_number, description, image_name) VALUES (11, '水瓶屋', '453-0000', '愛知県名古屋市１−１', '111-111-1111', '地元の人に愛される昔ながらの飲み屋さんです。', 'shop11.jpg' );
 INSERT IGNORE INTO shops (id, name, postal_code, address, phone_number, description, image_name) VALUES (12, 'うお屋', '453-0000', '愛知県名古屋市１−１', '111-111-1111', '地元の人に愛される昔ながらの和食屋さんです。', 'shop12.jpg' );
 INSERT IGNORE INTO shops (id, name, postal_code, address, phone_number, description, image_name) VALUES (13, 'SAMURAI', '453-0000', '愛知県名古屋市１−１', '111-111-1111', '地元の人に愛される昔ながらの飲み屋さんです。', 'shop10.jpg' );
 INSERT IGNORE INTO shops (id, name, postal_code, address, phone_number, description, image_name) VALUES (14, 'SAMURAI2', '453-0000', '愛知県名古屋市１−１', '111-111-1111', '地元の人に愛される昔ながらの飲み屋さんです。', 'shop11.jpg' );
 INSERT IGNORE INTO shops (id, name, postal_code, address, phone_number, description, image_name) VALUES (15, 'SAMURAI3', '453-0000', '愛知県名古屋市１−１', '111-111-1111', '地元の人に愛される昔ながらの和食屋さんです。', 'shop12.jpg' );
 
/* categoryテーブル */
INSERT IGNORE INTO categories (id, name) VALUES (1, "和食");
INSERT IGNORE INTO categories (id, name) VALUES (2, "洋食");
INSERT IGNORE INTO categories (id, name) VALUES (3, "中華");
INSERT IGNORE INTO categories (id, name) VALUES (4, "ラーメン");
INSERT IGNORE INTO categories (id, name) VALUES (5, "飲み屋");
INSERT IGNORE INTO categories (id, name) VALUES (6, "スイーツ");

/* reviewテーブル */
INSERT IGNORE INTO reviews (id, shop_id, user_id, review) VALUES (1, 1, 1, "とっても美味しくて大満足でした。また是非行きたいです。"); 
INSERT IGNORE INTO reviews (id, shop_id, user_id, review) VALUES (2, 2, 1, "とっても美味しくて大満足でした。また是非行きたいです。"); 
INSERT IGNORE INTO reviews (id, shop_id, user_id, review) VALUES (3, 3, 1, "とっても美味しくて大満足でした。また是非行きたいです。"); 
INSERT IGNORE INTO reviews (id, shop_id, user_id, review) VALUES (4, 4, 1, "とっても美味しくて大満足でした。また是非行きたいです。"); 
INSERT IGNORE INTO reviews (id, shop_id, user_id, review) VALUES (5, 1, 3, "少し騒がしく、あまり落ち着けませんでした。"); 
INSERT IGNORE INTO reviews (id, shop_id, user_id, review) VALUES (6, 2, 3, "少し騒がしく、あまり落ち着けませんでした。"); 
INSERT IGNORE INTO reviews (id, shop_id, user_id, review) VALUES (7, 3, 3, "少し騒がしく、あまり落ち着けませんでした。"); 
INSERT IGNORE INTO reviews (id, shop_id, user_id, review) VALUES (8, 4, 3, "少し騒がしく、あまり落ち着けませんでした。"); 
INSERT IGNORE INTO reviews (id, shop_id, user_id, review) VALUES (9, 1, 1, "とっても美味しくて大満足でした。また是非行きたいです。"); 
INSERT IGNORE INTO reviews (id, shop_id, user_id, review) VALUES (10, 1, 1, "とっても美味しくて大満足でした。また是非行きたいです。"); 
INSERT IGNORE INTO reviews (id, shop_id, user_id, review) VALUES (11, 1, 1, "とっても美味しくて大満足でした。また是非行きたいです。"); 
INSERT IGNORE INTO reviews (id, shop_id, user_id, review) VALUES (12, 1, 1, "とっても美味しくて大満足でした。また是非行きたいです。"); 
INSERT IGNORE INTO reviews (id, shop_id, user_id, review) VALUES (13, 1, 1, "とっても美味しくて大満足でした。また是非行きたいです。"); 
INSERT IGNORE INTO reviews (id, shop_id, user_id, review) VALUES (14, 1, 1, "とっても美味しくて大満足でした。また是非行きたいです。"); 
INSERT IGNORE INTO reviews (id, shop_id, user_id, review) VALUES (15, 1, 1, "とっても美味しくて大満足でした。また是非行きたいです。"); 
INSERT IGNORE INTO reviews (id, shop_id, user_id, review) VALUES (16, 1, 1, "とっても美味しくて大満足でした。また是非行きたいです。"); 
INSERT IGNORE INTO reviews (id, shop_id, user_id, review) VALUES (17, 1, 1, "とっても美味しくて大満足でした。また是非行きたいです。"); 
INSERT IGNORE INTO reviews (id, shop_id, user_id, review) VALUES (18, 1, 1, "とっても美味しくて大満足でした。また是非行きたいです。"); 
INSERT IGNORE INTO reviews (id, shop_id, user_id, review) VALUES (19, 1, 1, "とっても美味しくて大満足でした。また是非行きたいです。"); 
INSERT IGNORE INTO reviews (id, shop_id, user_id, review) VALUES (20, 1, 1, "とっても美味しくて大満足でした。また是非行きたいです。");

/* reservationテーブル */
INSERT IGNORE INTO reservations (id, shop_id, user_id, visit_date, visit_time, number_of_people) VALUES (1, 1, 3, '2024-11-01', '19:00:00', 2);
INSERT IGNORE INTO reservations (id, shop_id, user_id, visit_date, visit_time, number_of_people) VALUES (2, 1, 3, '2024-11-02', '19:00:00', 2);
INSERT IGNORE INTO reservations (id, shop_id, user_id, visit_date, visit_time, number_of_people) VALUES (3, 1, 3, '2024-11-03', '19:00:00', 2);
INSERT IGNORE INTO reservations (id, shop_id, user_id, visit_date, visit_time, number_of_people) VALUES (4, 1, 3, '2024-11-04', '19:00:00', 2);
INSERT IGNORE INTO reservations (id, shop_id, user_id, visit_date, visit_time, number_of_people) VALUES (5, 2, 3, '2024-11-01', '19:00:00', 2);
INSERT IGNORE INTO reservations (id, shop_id, user_id, visit_date, visit_time, number_of_people) VALUES (6, 2, 3, '2024-11-02', '19:00:00', 2);
INSERT IGNORE INTO reservations (id, shop_id, user_id, visit_date, visit_time, number_of_people) VALUES (7, 2, 3, '2024-11-03', '19:00:00', 2);
INSERT IGNORE INTO reservations (id, shop_id, user_id, visit_date, visit_time, number_of_people) VALUES (8, 2, 3, '2024-11-04', '19:00:00', 2);
INSERT IGNORE INTO reservations (id, shop_id, user_id, visit_date, visit_time, number_of_people) VALUES (9, 2, 3, '2024-11-05', '20:00:00', 2);
INSERT IGNORE INTO reservations (id, shop_id, user_id, visit_date, visit_time, number_of_people) VALUES (10, 2, 3, '2024-11-06', '20:00:00', 2);
INSERT IGNORE INTO reservations (id, shop_id, user_id, visit_date, visit_time, number_of_people) VALUES (11, 2, 3, '2024-11-07', '20:00:00', 2);
INSERT IGNORE INTO reservations (id, shop_id, user_id, visit_date, visit_time, number_of_people) VALUES (12, 2, 3, '2024-11-08', '20:00:00', 2);

/* favoriteテーブル */
INSERT IGNORE INTO favorites (id, shop_id, user_id) VALUES (1, 1, 3);
INSERT IGNORE INTO favorites (id, shop_id, user_id) VALUES (2, 2, 3);
INSERT IGNORE INTO favorites (id, shop_id, user_id) VALUES (3, 3, 3);

/* shop_categoryテーブル */
INSERT IGNORE INTO shop_category (id, shop_id, category_id) VALUES (1, 1, 1);
INSERT IGNORE INTO shop_category (id, shop_id, category_id) VALUES (2, 1, 4);
INSERT IGNORE INTO shop_category (id, shop_id, category_id) VALUES (3, 1, 5);
INSERT IGNORE INTO shop_category (id, shop_id, category_id) VALUES (4, 2, 1);
INSERT IGNORE INTO shop_category (id, shop_id, category_id) VALUES (5, 2, 5);
INSERT IGNORE INTO shop_category (id, shop_id, category_id) VALUES (6, 3, 1);
INSERT IGNORE INTO shop_category (id, shop_id, category_id) VALUES (7, 3, 8);
INSERT IGNORE INTO shop_category (id, shop_id, category_id) VALUES (8, 4, 1);
INSERT IGNORE INTO shop_category (id, shop_id, category_id) VALUES (9, 5, 1);
INSERT IGNORE INTO shop_category (id, shop_id, category_id) VALUES (10, 6, 1);
INSERT IGNORE INTO shop_category (id, shop_id, category_id) VALUES (11, 7, 1);
INSERT IGNORE INTO shop_category (id, shop_id, category_id) VALUES (12, 8, 1);
INSERT IGNORE INTO shop_category (id, shop_id, category_id) VALUES (13, 9, 6);
INSERT IGNORE INTO shop_category (id, shop_id, category_id) VALUES (14, 10, 6);
INSERT IGNORE INTO shop_category (id, shop_id, category_id) VALUES (15, 11, 6);
INSERT IGNORE INTO shop_category (id, shop_id, category_id) VALUES (16, 12, 1);
INSERT IGNORE INTO shop_category (id, shop_id, category_id) VALUES (17, 12, 5);
INSERT IGNORE INTO shop_category (id, shop_id, category_id) VALUES (18, 12, 6);
INSERT IGNORE INTO shop_category (id, shop_id, category_id) VALUES (19, 13, 1);
INSERT IGNORE INTO shop_category (id, shop_id, category_id) VALUES (20, 14, 1);
INSERT IGNORE INTO shop_category (id, shop_id, category_id) VALUES (21, 15, 5);