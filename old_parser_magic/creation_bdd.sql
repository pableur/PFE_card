GRANT ALL PRIVILEGES ON magic.* to LENON@192.168.1.15 IDENTIFIED BY 'magicpswd';
GRANT ALL PRIVILEGES ON magic.* to LENON@192.168.1.29 IDENTIFIED BY 'magicpswd';

GRANT ALL PRIVILEGES ON *.* to LENON@"%" IDENTIFIED BY 'magicpswd';


DROP TABLE edition;
DROP TABLE MagicCorpoEdition;

DROP TABLE card;
CREATE TABLE card
(
	id int NOT NULL AUTO_INCREMENT,
	nameVO VARCHAR(100),
	nameVF VARCHAR(100),
	level int,
	color VARCHAR(100),
	idEdition INT,
	typeVO VARCHAR(100),
	typeVF VARCHAR(100),
	cout VARCHAR(100),
	strong INT,
	endu INT,
	capaciteVO TEXT,
	capaciteVF TEXT,
	image VARCHAR(255),
	url VARCHAR(100),
	PRIMARY KEY (id)
);

DROP TABLE cardURL;
CREATE TABLE cardURL
(
	idCard int,
	urlMagicCorpo VARCHAR(255)
);

DROP TABLE cardPrice;
CREATE TABLE cardPrice
(
	idCard int,
	magicCorpo float
	PRIMARY KEY (idCard)
);

DROP TABLE Price;
CREATE TABLE Price(
	id INT NOT NULL AUTO_INCREMENT,
	idCard INT,
	idEtat INT,
	idEdition INT,
	date TIMESTAMP,
	price FLOAT,
	boutique VARCHAR(255),
	PRIMARY KEY (id)
);

DROP TABLE Etat;
CREATE TABLE Etat(
	id INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(255),
	description VARCHAR(255),
	PRIMARY KEY (id)
);

DROP TABLE type;
CREATE TABLE type
(
	id int,
	nameVO VARCHAR(255),
	nameVF VARCHAR(255)
);

CREATE TABLE edition
(
	id int NOT NULL AUTO_INCREMENT,
	nameVO VARCHAR(255),
	nameVF VARCHAR(255),
	logo VARCHAR(255),
	icon VARCHAR(255),
	nombreCard int,
	dateExtension VARCHAR(255),
	pack VARCHAR(255),
	PRIMARY KEY (id)
);

CREATE TABLE pack
(
	id int NOT NULL AUTO_INCREMENT,
	name VARCHAR(255),
	PRIMARY KEY (id)
);

CREATE TABLE MagicCorpoEdition
(
	id int NOT NULL AUTO_INCREMENT,
	idEdition int,
	URL VARCHAR(255),
	PRIMARY KEY (id)
);

DROP TABLE ConnexionInfo;
CREATE TABLE ConnexionInfo(
	id int NOT NULL AUTO_INCREMENT,
	date TIMESTAMP,
	ip VARCHAR(20),
	idCard int,
	nameOCR VARCHAR(255),
	similitude FLOAT,
	good BOOLEAN,
	dir VARCHAR(255),
	analyseTime float,
	ocrTime float,
	findTime float,
	idAnalyse int,
	PRIMARY KEY (id)
);

DROP TABLE ErreurRegister;
CREATE TABLE ErreurRegister
(
	id int NOT NULL AUTO_INCREMENT,
	idAnalyse int,
	erreurName VARCHAR(255),
	PRIMARY KEY (id)
);

DROP TABLE TimeRegister;
CREATE TABLE TimeRegister
(
	id int NOT NULL AUTO_INCREMENT,
	idAnalyse int,
	marqueur VARCHAR(255),
	time	int,
	PRIMARY KEY (id)
);
