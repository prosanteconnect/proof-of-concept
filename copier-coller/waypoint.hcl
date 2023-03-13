project = "copier-coller"

runner {
  enabled = true
  profile = "psc-pocs"
  data_source "git" {
    url = "https://github.com/prosanteconnect/proof-of-concept.git"
    ref = "main"
	path = "copier-coller/"
	ignore_changes_outside_path = true
  }
  poll {
    enabled = false
  }
}


app "demo-app-1" {
  build {
    use "docker" {
      dockerfile = "${path.app}/demo-app-1/Dockerfile"
    }
    registry {
      use "docker" {
        image = "${var.registry_username}/demo-app-1"
        tag = gitrefpretty()
        username = var.registry_username
        password = var.registry_password
        local = true
      }
    }
  }

  deploy {
    use "nomad-jobspec" {
      jobspec = templatefile("${path.app}/demo-app-1/app1.nomad.tpl", {
        datacenter = var.datacenter
        nomad_namespace = var.nomad_namespace
        log_level = var.log_level
      })
    }
  }
}

app "demo-app-2" {
  build {
    use "docker" {
      dockerfile = "${path.app}/demo-app-2/Dockerfile"
    }
    registry {
      use "docker" {
        image = "${var.registry_username}/demo-app-2"
        tag = gitrefpretty()
        username = var.registry_username
        password = var.registry_password
        local = true
      }
    }
  }

  deploy {
    use "nomad-jobspec" {
      jobspec = templatefile("${path.app}/demo-app-2/app2.nomad.tpl", {
        datacenter = var.datacenter
        nomad_namespace = var.nomad_namespace
        log_level = var.log_level
      })
    }
  }
}

variable "datacenter" {
  type    = string
  default = ""
  env     = ["NOMAD_DATACENTER"]
}

variable "nomad_namespace" {
  type    = string
  default = ""
  env     = ["NOMAD_NAMESPACE"]
}

variable "registry_username" {
  type      = string
  default   = ""
  env       = ["REGISTRY_USERNAME"]
  sensitive = true
}

variable "registry_password" {
  type      = string
  default   = ""
  env       = ["REGISTRY_PASSWORD"]
  sensitive = true
}

variable "log_level" {
  type    = string
  default = "INFO"
}
