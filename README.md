# 플레이닷 API

> 야구 팬들을 위한 승부예측, 채팅 서비스 API

KBO 경기를 크롤링하여 승부예측 및 승리요정이라는 통계를 제공합니다.
팬들을 위해 경기 진행 중, 각 경기별 채팅방에 입장해 소통할 수 있습니다.

## 기술스택

<p align="center">
<img src="https://img.shields.io/badge/Java 17-DD0700?style=flat&logoColor=white">
<img src="https://img.shields.io/badge/SpringBoot 3.1.6-6DB33F?style=flat&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/Spring Data JPA-6DB33F?style=flat&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/QueryDSL-0769AD?style=flat&logoColor=white">
<img src="https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white">
<img src="https://img.shields.io/badge/JUnit5-25A162?style=flat&logo=junit5&logoColor=white">
<img src="https://img.shields.io/badge/Gradle-02303A?style=flat&logo=gradle&logoColor=white">
<img src="https://img.shields.io/badge/Github Actions-2088FF?style=flat&logo=githubactions&logoColor=white">
<img src="https://img.shields.io/badge/AWS EC2-FF9900?style=flat&logo=amazonec2&logoColor=white">
<img src="https://img.shields.io/badge/AWS S3-569A31?style=flat&logo=amazons3&logoColor=white">
</p>

## 프로젝트 설정

### resources-env/dev/application.yml

```yaml
server:
  servlet:
    encoding:
      charset: utf-8
      force: true
  port: #Port
spring:
  profiles:
    active:
      - prod
  datasource:
    url: #DB Host
    driver-class-name: #DB Driver
    username: #DB Username
    password: #DB Password
  jpa:
    hibernate:
      ddl-auto: none
    properties:
      hibernate:
        default_batch_fetch_size: 100
#OAuth2 Config
security:
  oauth2:
    client:
      registration:
        google:
          client-id: #Google Client Id
          client-name: google
          client-secret: #Google Client Secret
          client-authentication-method: #Google Authentication Method
          authorization-grant-type: #Google Grant Type
          redirect-uri: #Google Redirect Uri
          scope: email
        kakao:
          client-id: #Kakao Client Id
          client-name: kakao
          client-secret: #Kakao Client Secret
          client-authentication-method: #Kakao Authentication Method
          authorization-grant-type: #Kakao Grant Type
          redirect-uri: #Kakao Redirect Uri
          scope: account_email
        naver:
          client-id: #Naver Client Id
          client-name: naver
          client-secret: #Naver Client Secret
          client-authentication-method: #Naver Authentication Method
          authorization-grant-type: #Naver Grant Type
          redirect-uri: #Naver Redirect Uri
          scope: email
          state: #Naver State Value
      provider:
        kakao:
          authorization-uri: https://kauth.kakao.com/oauth/authorize
          token-uri: https://kauth.kakao.com/oauth/token
          user-info-uri: https://kapi.kakao.com/v2/user/me
          user-name-attribute: id
        naver:
          authorization-uri: https://nid.naver.com/oauth2.0/authorize
          token-uri: https://nid.naver.com/oauth2.0/token
          user-info-uri: https://openapi.naver.com/v1/nid/me
          user-name-attribute: response
        google:
          authorization-uri: https://accounts.google.com/o/oauth2/v2/auth
          token-uri: https://oauth2.googleapis.com/token
          user-info-uri: https://www.googleapis.com/userinfo/v2/me


#S3 Config
cloud:
  aws:
    s3:
      bucket: #S3 Bucket Name
      domain: #S3 Domain
    credentials:
      access-key: #S3 Access Key
      secret-key: #S3 Secret Key
    region:
      static: #S3 Region ex)ap-northeast-2
      auto: false
    stack:
      auto: false

my-env:
  #JWT Key Config
  jwt:
    key: #JWT Key
  #KBO Crawling Config
  chrome-driver:
    path: /usr/local/bin/chromedriver/chromedriver
```

### 빌드

```sh
./gradlew clean build
```

### 실행

```sh
java -jar ./build/libs/BaseballPrediction-0.0.1-SNAPSHOT.jar
```

## API 명세서

자세한 내용은 [위키](https://github.com/Team-TMB/backend/wiki/REST-API-Reference)를 참고해주세요.

<!-- Markdown link & img dfn's -->

[npm-image]: https://img.shields.io/npm/v/datadog-metrics.svg?style=flat-square

[npm-url]: https://npmjs.org/package/datadog-metrics

[npm-downloads]: https://img.shields.io/npm/dm/datadog-metrics.svg?style=flat-square

[travis-image]: https://img.shields.io/travis/dbader/node-datadog-metrics/master.svg?style=flat-square

[travis-url]: https://travis-ci.org/dbader/node-datadog-metrics

