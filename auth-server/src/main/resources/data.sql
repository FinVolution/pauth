--- default admin account
INSERT INTO `pauth`.`user` (`id`, `email`, `name`, `checkcode`, `password`, `roles`) VALUES ('1', 'admin@xxx.com', 'admin', 'ZO]7>u#-', '6E1DE661E721E588A0DB5DCDDFE5861A', 'admin,local');

insert into `pauth`.`access_token`(`id`,`token_value`,`expiration`,`token_type`,`refresh_token_id`,`auth_holder_id`,`user_id`,`client_id`,`insert_time`,`insert_by`,`update_time`,`update_by`,`is_active`)
values (1,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInVzZXJfcm9sZSI6ImFkbWluLGxvY2FsIiwidXNlcl9tYWlsIjoiYWRtaW5AeHh4LmNvbSIsInVzZXJfbmFtZSI6ImFkbWluIiwiaXNzIjoicGF1dGgiLCJleHAiOjE5MDU2NzM2NzcsImlhdCI6MTU5MDA1NDQ3NywianRpIjoiNjY5NzY2ZGQtYjRjNC00MzExLWJlMjktNWI0NDdjODBhYWJkIn0.yNLEfoHdNzpxt2MMZuIPvCWWAdg4ySkdx50PxpLkdLs','2030-05-22 17:47:57','Bearer',null,9,1,'atlas','2020-05-21 17:48:02','admin','2020-05-21 17:48:02','admin',1);

insert into `pauth`.`client`(`id`,`description`,`reuse_refresh_tokens`,`client_id`,`client_secret`,`basic_auth`,`redirect_url`,`owner_id`,`insert_time`,`insert_by`,`update_time`,`update_by`,`is_active`)
values (1,'',0,'atlas','PRBhLE','Basic YXRsYXM6UFJCaExF','.*',1,'2020-05-19 17:36:24','admin','2020-05-19 17:36:24','admin',1);
insert into `pauth`.`client`(`id`,`description`,`reuse_refresh_tokens`,`client_id`,`client_secret`,`basic_auth`,`redirect_url`,`owner_id`,`insert_time`,`insert_by`,`update_time`,`update_by`,`is_active`)
values (2,'',0,'stargate','qnIZdZ','Basic c3RhcmdhdGU6cW5JWmRa','.*',1,'2020-05-21 10:33:39','admin','2020-05-21 10:33:39','admin',1);
