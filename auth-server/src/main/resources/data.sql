--- default admin account
INSERT INTO `pauth`.`user` (`id`, `email`, `name`, `checkcode`, `password`, `roles`) VALUES ('1', 'admin@xxx.com', 'admin', 'ZO]7>u#-', '6E1DE661E721E588A0DB5DCDDFE5861A', 'admin,local');

--- default docker client
INSERT INTO `pauth`.`client` (`description`, `reuse_refresh_tokens`, `client_id`, `client_secret`, `basic_auth`, `redirect_url`, `owner_id`) VALUES ('docker registry', '0', 'docker', 'docker_secret', '', '', '1');