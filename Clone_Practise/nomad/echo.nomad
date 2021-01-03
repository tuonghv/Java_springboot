job "http-echo" {
  datacenters = ["dc1"]  
  
  group "echo" {
    count = 1
	
    network {
      port "pingport" {
        static = 8080
      }
    }

    service {
      name = "echoscv"
      tags = ["global", "echo"]
      port = "pingport"
    }
	
    task "server" {
      driver = "docker"      
	  
	  config {
        image = "hashicorp/http-echo:latest"
        args  = [
          "-listen", ":8080",
          "-text", "Hello and welcome to 127.0.0.1 running on port 8080",
        ]
		ports = ["pingport"]

      }      
	  
	  resources {
      }
    }
  }
}