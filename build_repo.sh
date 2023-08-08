#!/usr/bin/env bash
set -e
#*******************************************************************************
# Copyright French Prime minister Office/SGMAP/DINSIC/Vitam Program (2015-2019)
#
# contact.vitam@culture.gouv.fr
#
# This software is a computer program whose purpose is to implement a digital archiving back-office system managing
# high volumetry securely and efficiently.
#
# This software is governed by the CeCILL 2.1 license under French law and abiding by the rules of distribution of free
# software. You can use, modify and/ or redistribute the software under the terms of the CeCILL 2.1 license as
# circulated by CEA, CNRS and INRIA at the following URL "http://www.cecill.info".
#
# As a counterpart to the access to the source code and rights to copy, modify and redistribute granted by the license,
# users are provided only with a limited warranty and the software's author, the holder of the economic rights, and the
# successive licensors have only limited liability.
#
# In this respect, the user's attention is drawn to the risks associated with loading, using, modifying and/or
# developing or reproducing the software by the user in light of its specific status of free software, that may mean
# that it is complicated to manipulate, and that also therefore means that it is reserved for developers and
# experienced professionals having in-depth computer knowledge. Users are therefore encouraged to load and test the
# software's suitability as regards their requirements in conditions enabling the security of their systems and/or data
# to be ensured and, more generally, to use and operate it in the same conditions as regards security.
#
# The fact that you are presently reading this means that you have had knowledge of the CeCILL 2.1 license and that you
# accept its terms.
#*******************************************************************************
if [[ $1 != 'deb' ]] && [[ $1 != 'rpm' ]] ; then
  echo 'You must specify target to build !'
  echo './build_repo.sh deb|rpm [URL_TO_REPOSITORY]'
  exit 1;
fi

if  [ ! -z $2 ] ; then
  REPOSITORY=$2
else
  COMMIT_HASH=$(curl -s http://pic-prod-repository.vitam-factory/griffins/${VITAM_COMMIT:-master})
  REPOSITORY=https://pic-prod-repository.vitam-factory/griffins/${COMMIT_HASH}/
fi

WORKING_DIR=$(dirname $0)/$1

SOURCES_FILE=${WORKING_DIR}/sources # Contains all the urls where download rpm/deb
TARGET_DIR=${WORKING_DIR}/target      # Targer dir where copying rpm/deb dowloaded


mkdir -p ${TARGET_DIR}

if [ -f "${SOURCES_FILE}" ]
then
	cat ${SOURCES_FILE} |
	while read FILENAME
	do
		if [[ $(echo "${FILENAME}" | grep -E -o '^[^#]') ]] # skip is the line is commented
		then
		  echo "FILENAME : ${FILENAME}"
			if [ -f "${TARGET_DIR}/${FILENAME}" ]
			then
			 	echo "${FILENAME} already exists in ${TARGET_DIR} ! Skipping..."
			else # if [ -f "${TARGET_DIR}/${FILENAME}" ]
			  SRC_URL="${REPOSITORY}/$1/${FILENAME}"
			 	echo "Downloading ${SRC_URL} into ${TARGET_DIR}..."
			 	HTTP_CODE=$(curl -k --silent -o ${TARGET_DIR}/${FILENAME}.tmp 	--write-out "%{http_code}" ${SRC_URL})
			 	if [[ ${HTTP_CODE} -lt 200 || ${HTTP_CODE} -gt 299 ]] ; then
            echo "Cannot find ${FILENAME} in ${SRC_URL}"
            exit 1;
        fi
			 	mv ${TARGET_DIR}/${FILENAME}.tmp ${TARGET_DIR}/${FILENAME}
			 	echo "Download done."
			fi 
		fi
	done
else # if [ -f "${SOURCES_FILE}" ]
	echo "${SOURCES_FILE} doesn't exists  ! Exiting..."
fi