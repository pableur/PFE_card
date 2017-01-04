#!/usr/bin/env python
# -*- coding: utf-8 -*-

import mysql.connector 

class Edition():
	def __init__(self):
		self.id=0
		self.name=""

	def all(self):	
		try:
			conn = mysql.connector.connect(host="localhost",user="root",password="magicpswd", database="magic")
			cursor = conn.cursor()
			cursor.execute("""SELECT idEdition,URL FROM editionURL""")
			rows = cursor.fetchall()
			return rows

		except Exception as e:
			print("Erreur")
		    	print e
			return False
	
		conn.close()
