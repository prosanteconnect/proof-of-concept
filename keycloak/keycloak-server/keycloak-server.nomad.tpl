job "keycloak-server" {

    namespace = "keycloak"

    type = "service"

    datacenters = ["${datacenter}"]

    update {
        stagger = "30s"
        max_parallel = 1
    }
	
	vault {
		policies = ["keycloak"]
		change_mode = "restart"
	}
	
    group "keycloak-server" {
        count = 1
        network {
            mode = "host"
            port "http-port" { to = 8080 }
			port "https-port" { to = 8443 }
        }
		
        task "keycloak-server" {

            driver = "docker"
			
			artifact {
				source = "https://repo.forge.ans.henix.fr/artifactory/AC/TEST/CL/truststore.jks"
			}

            config {
                image = "${image}:${tag}"				
                ports = ["http-port", "https-port"]
				mount {
					type = "bind"
					target = "/opt/bitnami/keycloak/certs/truststore.jks"
					source = "local/truststore.jks"
					readonly = "false"
					bind_options {
						propagation = "rshared"
					}
				}
				
				mount {
				  type = "bind"
				  target = "/opt/bitnami/keycloak/certs/tls.pem"
				  source = "local/tls.pem"
				  readonly = false
				  bind_options {
					propagation = "rshared"
				  }
				}
				
				mount {
				  type = "bind"
				  target = "/opt/bitnami/keycloak/certs/tls.key"
				  source = "local/tls.key"
				  readonly = false
				  bind_options {
					propagation = "rshared"
				  }
				}
            }
			
			template {
				data = <<EOF
{{ with secret "keycloak/keycloak-server" }}{{ .Data.data.keycloak_server_tls_pem }}{{ end }}
EOF
				destination = "local/tls.pem"
			}
			
			template {
				data = <<EOF
{{ with secret "keycloak/keycloak-server" }}{{ .Data.data.keycloak_server_tls_key }}{{ end }}
EOF
				destination = "local/tls.key"
			}
			
			template {
				data = <<EOH
					KEYCLOAK_ADMIN_USER = {{ with secret "keycloak/keycloak-server" }}{{ .Data.data.keycloak_admin_user }}{{ end }}
					KEYCLOAK_ADMIN_PASSWORD = {{ with secret "keycloak/keycloak-server" }}{{ .Data.data.keycloak_admin_password }}{{ end }}
					KEYCLOAK_DATABASE_HOST = {{ range service "keycloak-db"}}{{ .Address }}{{ end }}
					KEYCLOAK_DATABASE_PORT = {{ range service "keycloak-db"}}{{ .Port }}{{ end }}
					KEYCLOAK_DATABASE_USER = {{ with secret "keycloak/keycloak-db" }}{{ .Data.data.pgsql_user }}{{ end }}
					KEYCLOAK_DATABASE_PASSWORD = {{ with secret "keycloak/keycloak-db" }}{{ .Data.data.pgsql_password }}{{ end }}
					KEYCLOAK_DATABASE_NAME = {{ with secret "keycloak/keycloak-db" }}{{ .Data.data.pgsql_dbname }}{{ end }}
					KEYCLOAK_ENABLE_HTTPS = true
					KEYCLOAK_HTTPS_USE_PEM = true
					KEYCLOAK_HTTPS_CERTIFICATE_FILE = /opt/bitnami/keycloak/certs/tls.pem
					KEYCLOAK_HTTPS_CERTIFICATE_KEY_FILE = /opt/bitnami/keycloak/certs/tls.key
					KEYCLOAK_HTTPS_TRUST_STORE_PASSWORD = {{ with secret "keycloak/keycloak-server" }}{{ .Data.data.keycloak_server_truststore_password }}{{ end }}
					
				EOH
				
				destination = "secrets/file.env"
				env         = true
			}
			
            resources {
                cpu = 1000
                memory = 2000
            }
	    	   
            service {
                name = "keycloak-server"
                port = "https-port"
                check {
                    name         = "alive"
                    type         = "tcp"
                    interval     = "10s"
                    timeout      = "5s"
                    port         = "https-port"
                }
            }
        }
    }
}
