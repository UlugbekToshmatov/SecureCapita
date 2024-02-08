-- For keeping records of user roles
DROP TABLE IF EXISTS Roles CASCADE;

CREATE TABLE Roles
(
    id            SERIAL NOT NULL PRIMARY KEY,
    name          VARCHAR(50) NOT NULL,
    permission    VARCHAR(255) NOT NULL,
    deleted       BOOLEAN DEFAULT FALSE,
    created_date  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT UQ_Roles_Name UNIQUE (name)
);

-- For keeping records of users' information
DROP TABLE IF EXISTS Users CASCADE;

CREATE TABLE Users
(
    id            BIGSERIAL NOT NULL PRIMARY KEY,
    first_name    VARCHAR(50) NOT NULL,
    last_name     VARCHAR(50) NOT NULL,
    email         VARCHAR(100) NOT NULL,
    password      VARCHAR(255) NOT NULL,
    address       VARCHAR(255) DEFAULT NULL,
    phone         VARCHAR(30) DEFAULT NULL,
    title         VARCHAR(50) DEFAULT NULL,
    bio           VARCHAR(255) DEFAULT NULL,
    enabled       BOOLEAN DEFAULT FALSE,
    locked        BOOLEAN DEFAULT FALSE,
    using_mfa     BOOLEAN DEFAULT FALSE,
    deleted       BOOLEAN DEFAULT FALSE,
    created_date  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    image_url     VARCHAR(255) DEFAULT 'https://cdn-icons-png.flaticon.com/512/149/149071.png',
    role_id       INTEGER NOT NULL,
    FOREIGN KEY (role_id) REFERENCES Roles(id) ON DELETE CASCADE ON UPDATE CASCADE/*CASCADE*/,
    CONSTRAINT UQ_Users_Email UNIQUE (email)
);

-- For keeping records of types of activities users can perform
DROP TABLE IF EXISTS Events CASCADE;

CREATE TABLE Events
(
    id            BIGSERIAL NOT NULL PRIMARY KEY,
    type          VARCHAR(50) NOT NULL CHECK(type IN ('LOGIN_ATTEMPT', 'LOGIN_ATTEMPT_FAILURE', 'LOGIN_ATTEMPT_SUCCESS', 'PROFILE_UPDATE', 'PROFILE_PICTURE_UPDATE', 'ROLE_UPDATE', 'ACCOUNT_SETTINGS_UPDATE', 'PASSWORD_UPDATE', 'MFA_UPDATE')),
    description   VARCHAR(255) NOT NULL,
    deleted       BOOLEAN DEFAULT FALSE,
    created_date  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT UQ_Events_Type UNIQUE (type)
);

-- For keeping records of user activities
DROP TABLE IF EXISTS UsersEvents CASCADE;

CREATE TABLE UsersEvents
(
    id            BIGSERIAL NOT NULL PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    event_id      BIGINT NOT NULL,
    device        VARCHAR(255) DEFAULT NULL,
    ip_address    VARCHAR(255) DEFAULT NULL,
    deleted       BOOLEAN DEFAULT FALSE,
    created_date  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (event_id) REFERENCES Events(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- For keeping records of account creations
DROP TABLE IF EXISTS AccountVerifications CASCADE;

CREATE TABLE AccountVerifications
(
    id            BIGSERIAL NOT NULL PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    url           VARCHAR(255) NOT NULL,
    deleted       BOOLEAN DEFAULT FALSE,
    created_date  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT UQ_AccountVerifications_User_Id_Url UNIQUE (user_id, url)
--     CONSTRAINT UQ_AccountVerifications_Url UNIQUE (url)
);

-- For keeping records of password resets
DROP TABLE IF EXISTS ResetPasswordVerifications CASCADE;

CREATE TABLE ResetPasswordVerifications
(
    id              BIGSERIAL NOT NULL PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    url             VARCHAR(255) NOT NULL,
    deleted         BOOLEAN DEFAULT FALSE,
    expiration_date TIMESTAMP NOT NULL,
    created_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT UQ_ResetPasswordVerifications_User_Id_Url UNIQUE (user_id, url)
--     CONSTRAINT UQ_ResetPasswordVerifications_Url UNIQUE (url)
);

-- For keeping records of two factor authentications
DROP TABLE IF EXISTS TwoFactorVerifications CASCADE;

CREATE TABLE TwoFactorVerifications
(
    id              BIGSERIAL NOT NULL PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    code            VARCHAR(10) NOT NULL,
    deleted         BOOLEAN DEFAULT FALSE,
    expiration_date TIMESTAMP NOT NULL,
    created_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT UQ_TwoFactorVerifications_User_Id_Code UNIQUE (user_id, code)
--     CONSTRAINT UQ_TwoFactorVerifications_Code UNIQUE (code)
);