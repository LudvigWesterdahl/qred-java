#!/bin/bash

readonly PROG_PATH=${0}
readonly PROG_DIR=${0%/*}
readonly PROG_NAME=$(basename $0)
readonly CURR_DIR=$(basename $(pwd))
readonly DOCKER_NETWORK="qred-java-network"

notify() {
    echo "${PROG_NAME}: $1"
    return 0
}

print_usage() {

    echo "Usage: ${PROG_NAME} <arg> [options...]"
    echo
    echo "Arguments:"
    echo " dbcreate           creates the db container"
    echo " dbstart            starts the db container"
    echo " dbstarta           starts the db container in the foreground (attached mode)"
    echo " dbstop             stops the db container"
    echo " dbdelete           deletes the db container"
    echo " mvnh               mvn help for plugin"
    echo
    echo "Options:"
    echo " -v, <version>      specifies tag and image version"
    echo " -p, <host port>    specifies host port for the docker container"
    echo " -w, <db password>  specifies the db password"
    echo " -u, <mvn plugin>   used together with mvnh to display plugin help"
    echo " -g, <mvn goal>     used together with mvnh to display plugin goal help"
    echo " -e, <profile>      the spring profile"

    return 0
}

main() {
    readonly SERVICE_NAME="service-qred-java"
    readonly DB_SERVICE_NAME="db-"${SERVICE_NAME}
    
    declare -ri NUM_ARGS=1

    if [ "${1}" == "-h" ] || [ "${1}" == "--help" ]; then
        print_usage
        return 0
    fi

    if [ $# -lt ${NUM_ARGS} ]; then
        notify "try '${PROG_NAME} -h' or '${PROG_NAME} --help' for more information"
        return 1
    fi
    
    declare ARG=${1}

    declare V_FLAG=""
    declare P_FLAG=""
    declare W_FLAG=""
    declare U_FLAG=""
    declare G_FLAG=""
    declare E_FLAG=""

    declare -i I=$NUM_ARGS
    I=I+1
    while [ $I -le $# ]; do
        case ${!I} in
          "-v")
            I=I+1
            V_FLAG=${!I}
            ;;
          "-p")
            I=I+1
            P_FLAG=${!I}
          ;;
          "-w")
            I=I+1
            W_FLAG=${!I}
          ;;
          "-u")
            I=I+1
            U_FLAG=${!I}
          ;;
          "-g")
            I=I+1
            G_FLAG=${!I}
          ;;
          "-e")
            I=I+1
            E_FLAG=${!I}
          ;;
          *)
            notify "${!I} does not match any supported option"
            return 1
          ;;
        esac
        I=I+1
    done

    declare DOCKER_CONTAINER="${SERVICE_NAME}"

    if [ "${ARG}" == "dbcreate" ]; then
      if [ "${W_FLAG}" == "" ]; then
          notify "missing -w flag for ${ARG}"
          return 1
      fi

      if [[ "${P_FLAG}" == "" ]]; then
          notify "missing -p flag for ${ARG}"
          return 1
      fi

      docker network inspect ${DOCKER_NETWORK} 1>/dev/null 2>/dev/null

      if [ $? -ne 0 ]; then
          notify "docker network ${DOCKER_NETWORK} does not exist, creating..."
          docker network create ${DOCKER_NETWORK}
      fi
      
      declare DOCKER_PORT=""
      if [ "${P_FLAG}" != "" ]; then
          DOCKER_PORT="-p ${P_FLAG}:5432"
      fi
	
      docker container create \
             --name ${DB_SERVICE_NAME} \
             --network ${DOCKER_NETWORK} \
             --log-driver local \
             --memory=150m \
             -e POSTGRES_USER=payment \
             -e POSTGRES_PASSWORD=${W_FLAG} \
             -v ./.db-data:/var/lib/postgresql/data \
             ${DOCKER_PORT} \
             postgres:17-alpine
    
      return 0
    fi

    if [ "${ARG}" == "dbstart" ]; then
	    docker container start ${DB_SERVICE_NAME}
	    return 0
    fi

    if [ "${ARG}" == "dbstarta" ]; then
	    docker container start --attach ${DB_SERVICE_NAME}
	    return 0
    fi

    if [ "${ARG}" == "dbstop" ]; then
	    docker container stop ${DB_SERVICE_NAME}
	    return 0
    fi

    if [ "${ARG}" == "dbdelete" ]; then
      docker container stop ${DB_SERVICE_NAME} 1>/dev/null 2>/dev/null
      docker container rm ${DB_SERVICE_NAME}

      return 0
    fi

    if [ "${ARG}" == "mvnh" ]; then
      if [ "${U_FLAG}" == "" ]; then
          mvn help:help
          return 0
      fi
    
      if [ "${G_FLAG}" == "" ]; then
          mvn help:describe -e -Dplugin=${U_FLAG}
          return 0
      fi
    
      mvn help:describe -e -Dplugin=${U_FLAG} -Dgoal=${G_FLAG} -Ddetail=true
      return 0
    fi
    
    notify "no matching arg "${ARG}
    
    return 1
}

main "${@}"
