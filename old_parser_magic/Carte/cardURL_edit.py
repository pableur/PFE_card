#!/usr/bin/env python
# -*- coding: utf-8 -*-

import mysql.connector 

if __name__ == "__main__":
	try:
		conn = mysql.connector.connect(host="localhost",user="root",password="magicpswd", database="magic")
		cursor = conn.cursor()
		cursor.execute("""SELECT id, url FROM card""")
		rows = cursor.fetchall()
		
	except Exception as e:
		print "Erreur ",
	    	print e

	for i in rows:

		if len(i[1])>0:
			print i[0],
			print " ",
			print i[1]
			try:	
				cursor = conn.cursor()
				
				cursor.execute("""INSERT INTO cardURL(idCard,urlMagicCorpo) VALUES(%s,%s)""",(i[0],i[1]))
				conn.commit()
		
			except Exception as e:
				print "Erreur ",
			    	print e
				exit()
	conn.close()


