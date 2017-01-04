#! /usr/bin/python3.2
# -*- coding: utf-8 -*-

import mysql.connector 
import urllib2
import os

#///////////////////////////////////////////////
#
#	Telechargement de l'image
#
#///////////////////////////////////////////////
def download(url, name):
	url = url.decode("utf8")
	print url,"-->",
	try:
		opener = urllib2.build_opener(urllib2.HTTPCookieProcessor())
		image = opener.open(url)
	except :
		print "probleme de conexion", url
		return False

	# affiche le chemin si le DL a reussi
	try:
		output = open(str(name),'wb')
		output.write(image.read())
		output.close()
	except:
		print "probleme de download", url
		return False
	print os.path.abspath(''),'/',name
	return True

def findURL():
	
	try:
		conn = mysql.connector.connect(host="localhost",user="root",password="magicpswd", database="magic")
		cursor = conn.cursor()
		cursor.execute("""SELECT logo,icon FROM edition """)
		rows = cursor.fetchall()
		return rows		
		#conn.commit()
		conn.close()

	except Exception as e:
		print("Erreur")
		#conn.rollback()
	    	print e

def extractName(stringURL):
	return stringURL[57:] 
	
	

if __name__ == "__main__":
	path='image/edition'
	if not os.path.exists(path):
		os.makedirs(path)
		print "creation de "+path
	os.chdir(path)

	path='logo'
	if not os.path.exists(path):
		os.makedirs(path)
		print "creation de "+path
	
	path='icon'
	if not os.path.exists(path):
		os.makedirs(path)
		print "creation de "+path

	liste = findURL()
	for i in liste:
		if len(i[0])>0:
			download(i[0],'logo/'+extractName(i[0]))
		if len(i[1])>0:
			download(i[1],'icon/'+extractName(i[1]))

