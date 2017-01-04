#!/usr/bin/env python
# -*- coding: utf-8 -*-

import mysql.connector 
if __name__ == "__main__":
	try:
		conn = mysql.connector.connect(host="localhost",user="root",password="magicpswd", database="magic")
		cursor = conn.cursor()
		cursor.execute("""SELECT   id,name
				FROM     edition
				""")
		rows = cursor.fetchall()
		name=""
		idEdition=0
		for i in rows:
			print i
			

	except Exception as e:
		print("Erreur")
		#conn.rollback()
	    	print e
		
