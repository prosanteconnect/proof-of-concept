job "copier-coller-demo-app-1" {
  datacenters = ["${datacenter}"]
  type = "service"
   namespace = "copier-coller"

  vault {
    policies = ["copier-coller"]
    change_mode = "restart"
  }

  affinity {
    attribute = "$\u007Bnode.class\u007D"
    value = "standard"
  }

  group "demo-app-1" {
    count = "1"
    restart {
      attempts = 3
      delay = "60s"
      interval = "1h"
      mode = "fail"
    }

    network {
      port "http" {
        to = 8080
      }
    }

    task "demo-app-1" {
      driver = "docker"

      config {
        image = "${artifact.image}:${artifact.tag}"
        ports = ["http"]
      }

      # env variables
      template {
        destination = "local/file.env"
        env = true
        data = <<EOH
PUBLIC_HOSTNAME={{ with secret "copier-coller/app" }}{{ .Data.data.demo_app_1_public_hostname }}{{ end }}
JAVA_TOOL_OPTIONS="-Xms256m -Xmx1g -XX:+UseG1GC -Dspring.config.location=/secrets/application.properties -Dlogging.level.fr.ans.psc=${log_level}"
EOH
      }

      # application.properties
      template {
        destination = "secrets/application.properties"
        change_mode = "restart"
        data = <<EOF
psc.context.sharing.api.url=http://{{ range service "copier-coller-api"}}{{ .Address }}:{{ .Port }}{{ end }}/cc-api/cache
server.use-forward-headers=true
server.forward-headers-strategy=NATIVE
server.tomcat.protocol-header=X-Forwarded-Proto
EOF
      }

      resources {
        cpu = 500
        memory = 1152
      }

      service {
        name = "$\u007BNOMAD_JOB_NAME\u007D"
        tags = ["urlprefix-app1-copier-coller"]
        port = "http"		
        check {
          type = "http"
          path = "/insecure/check"
          port = "http"
          interval = "30s"
          timeout = "2s"
          failures_before_critical = "3"
        }
      }
      }
  }
}
