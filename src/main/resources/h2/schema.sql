create table team (
    team_id int auto_increment primary key,
    name varchar(30) unique not null,
    short_name varchar(10) unique not null,
    logo_url varchar(200),
    color varchar(16),
    created_at datetime not null default current_timestamp,
    modified_at datetime
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create table member (
    member_id bigint auto_increment primary key,
    username varchar(100) unique not null,
    social varchar(20) not null,
    password varchar(200) not null,
    nickname varchar(20) unique not null,
    profile_image_url varchar(200),
    team_id int,
    member_comment varchar(100),
	token int default 0 not null,
    level int default 1 not null,
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

create table game_reply (
	game_reply_id bigint auto_increment primary key,
    member_id bigint not null,
    game_id bigint not null,
    content varchar(1000) not null,
    created_at datetime not null default current_timestamp,
    modified_at datetime,
    foreign key(member_id) references member(member_id),
    foreign key(game_id) references game(game_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create table game_reply_like(
	game_reply_like_id bigint auto_increment primary key,
    game_reply_id bigint not null,
    member_id bigint not null,
    created_at datetime not null default current_timestamp,
    modified_at datetime,
    foreign key(game_reply_id) references game_reply(game_reply_id),
    foreign key(member_id) references member(member_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create table mini_game (
	mini_game_id bigint auto_increment primary key,
	question varchar(100) not null,
    option1 varchar(100) not null,
    option2 varchar(100) not null,
    created_at datetime not null default current_timestamp,
    modified_at datetime
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
	foreign key(member_id) references member(member_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create table monthly_fairy_reply (
	monthly_fairy_reply_id bigint auto_increment primary key,
    member_id bigint not null,
    monthly_fairy_id bigint not null,
    content varchar(1000) not null,
    created_at datetime not null default current_timestamp,
    modified_at datetime,
    foreign key(member_id) references member(member_id),
    foreign key(monthly_fairy_id) references monthly_fairy(monthly_fairy_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create table monthly_fairy_reply_like(
	monthly_fairy_reply_like_id bigint auto_increment primary key,
    monthly_fairy_reply_id bigint not null,
    member_id bigint not null,
    created_at datetime not null default current_timestamp,
    modified_at datetime,
    foreign key(monthly_fairy_reply_id) references monthly_fairy_reply(monthly_fairy_reply_id),
    foreign key(member_id) references member(member_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;