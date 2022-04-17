INSERT INTO namespace (name) VALUES ('namespace_1');
INSERT INTO namespace (name) VALUES ('namespace_2');
INSERT INTO namespace (name) VALUES ('namespace_3');
INSERT INTO namespace (name) VALUES ('namespace_without_filenames');


INSERT INTO filename (name, namespace) VALUES ('filename_1_ns_1', 'namespace_1');
INSERT INTO filename (name, namespace) VALUES ('filename_2_ns_1', 'namespace_1');

INSERT INTO filename (name, namespace) VALUES ('filename_1_ns_2', 'namespace_2');
INSERT INTO filename (name, namespace) VALUES ('filename_2_ns_2', 'namespace_2');

INSERT INTO filename (name, namespace) VALUES ('filename_1_ns_3', 'namespace_3');
INSERT INTO filename (name, namespace) VALUES ('filename_2_ns_3', 'namespace_3');





