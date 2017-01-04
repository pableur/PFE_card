#!/usr/bin/env python
# -*- coding: utf-8 -*-

import mysql.connector 

def insert_name(value):
	try:
		conn = mysql.connector.connect(host="localhost",user="root",password="magicpswd", database="magic")
		cursor = conn.cursor()
		cursor.execute("INSERT INTO Etat(name) VALUES ('"+value+"') ")
		conn.commit()
			
	except Exception as e:
		print "Erreur ",
	    	print e


try:
	conn = mysql.connector.connect(host="localhost",user="root",password="magicpswd", database="magic")
	cursor = conn.cursor()
	cursor.execute("""SELECT   id,name FROM     edition""")
	rows = cursor.fetchall()						

except Exception as e:
	print("Erreur")
	#conn.rollback()
    	print e

for i in rows:
	if name== i[1]:
		self.idEdition=i[0]

insert_name("nm")
