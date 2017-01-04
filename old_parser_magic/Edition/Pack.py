#! /usr/bin/python3.2
# -*- coding: utf-8 -*-

import mysql.connector

if __name__ == "__main__":
	try:
		conn = mysql.connector.connect(host="localhost",user="root",password="magicpswd", database="magic")
		cursor = conn.cursor()
		cursor.execute("""SELECT   pack
				FROM     edition
				GROUP BY pack
				""")
		rows = cursor.fetchall()
		

		for i in rows:
			print str(i[0])
			cursor = conn.cursor()
			parm={i[0]}
			cursor.execute("""INSERT INTO pack(name) VALUES (%s) """,parm)
			conn.commit()
				
		conn.close()		

		
	except Exception as e:
		print("Erreur")
	    	print e


