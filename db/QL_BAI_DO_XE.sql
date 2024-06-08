

CREATE TABLE vehicle (
                                          id int IDENTITY(0,1) NOT NULL,
                                          license_plate varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                          entry_time varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                          exit_time varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
                                          parking_fee float NULL,
                                          status int DEFAULT 1 NULL
);