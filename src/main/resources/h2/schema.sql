create table team (
    team_id int auto_increment primary key,
    name varchar(30) unique not null,
    short_name varchar(10) unique not null,
    color varchar(16),
    created_at timestamp not null default current_timestamp,
    modified_at timestamp
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create table member (
    member_id bigint auto_increment primary key,
    username varchar(50) unique not null,
    social varchar(20) not null,
    password varchar(200) not null,
    nickname varchar(200) unique,
    profile_image_url varchar(200),
    team_id int,
    member_comment varchar(100),
	token int default 0 not null,
    level int default 1 not null,
    is_new_member tinyint(1) not null,
	created_at datetime not null default current_timestamp,
    modified_at datetime,
    foreign key(team_id) references team(team_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create table game (
	game_id bigint auto_increment primary key,
    home_team_id int not null,
    away_team_id int not null,
    status varchar(20) not null,
    started_at datetime not null,
    home_team_score int not null default 0,
    away_team_score int not null default 0,
    win_team_id int,
    created_at datetime not null default current_timestamp,
    modified_at datetime,
    foreign key(home_team_id) references team(team_id),
    foreign key(away_team_id) references team(team_id),
    foreign key(win_team_id) references team(team_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create table game_vote(
	game_vote_id bigint auto_increment primary key,
    game_id bigint not null,
    team_id int not null,
    member_id bigint not null,
    created_at datetime not null default current_timestamp,
    modified_at datetime,
    foreign key(game_id) references game(game_id),
    foreign key(team_id) references team(team_id),
    foreign key(member_id) references member(member_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create TABLE mini_game (
  mini_game_id bigint NOT NULL AUTO_INCREMENT,
  game_id bigint NOT NULL,
  member_id bigint NOT NULL,
  question varchar(100) NOT NULL,
  option1 varchar(100) NOT NULL,
  option2 varchar(100) NOT NULL,
  status varchar(20) NOT NULL,
  started_at datetime DEFAULT NULL,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modified_at datetime DEFAULT NULL,
  PRIMARY KEY (mini_game_id),
  KEY fk_mini_game_game_id (game_id),
  KEY fk_mini_game_member_id (member_id),
  CONSTRAINT fk_mini_game_game_id FOREIGN KEY (game_id) REFERENCES game (game_id),
  CONSTRAINT fk_mini_game_member_id FOREIGN KEY (member_id) REFERENCES member (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


create table mini_game_vote(
	mini_game_vote_id bigint auto_increment primary key,
    mini_game_id bigint not null,
    member_id bigint not null,
    vote_option int not null,
    created_at datetime not null default current_timestamp,
    modified_at datetime,
    foreign key(mini_game_id) references mini_game(mini_game_id),
    foreign key(member_id) references member(member_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create table gift_token_log(
	gift_token_log_id bigint auto_increment primary key,
    take_member_id bigint not null,
    give_member_id bigint not null,
    token_amount int not null,
    comment varchar(100),
    created_at datetime not null default current_timestamp,
    modified_at datetime,
    foreign key(take_member_id) references member(member_id),
    foreign key(give_member_id) references member(member_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create table monthly_fairy(
    monthly_fairy_id bigint auto_increment primary key,
    statistic_month int not null,
    type varchar(10) not null,
    fairy_rank int not null,
    vote_ratio int not null,
    member_id bigint not null,
    created_at datetime not null default current_timestamp,
    modified_at datetime,
	foreign key(member_id) references member(member_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create table reply(
	reply_id bigint auto_increment primary key,
	parent_reply_id bigint,
    member_id bigint not null,
    content varchar(1000) not null,
    reply_type varchar(10) not null,
    reply_status varchar(10) not null,
    created_at datetime not null default current_timestamp,
    modified_at datetime,
    foreign key(member_id) references member(member_id),
    foreign key(parent_reply_id) references reply(reply_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create table reply_like(
	reply_like_id bigint auto_increment primary key,
	member_id bigint not null,
	reply_id bigint not null,
	created_at datetime not null default current_timestamp,
	modified_at datetime,
	foreign key(reply_id) references reply(reply_id),
	foreign key(member_id) references member(member_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create table reply_report(
	reply_report_id bigint auto_increment primary key,
	member_id bigint not null,
	reply_id bigint not null,
	report_type varchar(10) not null,
	created_at datetime not null default current_timestamp,
	modified_at datetime,
	foreign key(reply_id) references reply(reply_id),
	foreign key(member_id) references member(member_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;