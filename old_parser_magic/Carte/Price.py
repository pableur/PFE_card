#!/usr/bin/env python
# -*- coding: utf-8 -*-

import mysql.connector 

class Price():
	def __init__(self,id,priceList):
		self.idCard=id
		self.priceList=priceList

	def update(self,site,price):
		self.verif()
		try:
			conn = mysql.connector.connect(host="localhost",user="root",password="magicpswd", database="magic")
			cursor = conn.cursor()
			requete='UPDATE cardPrice SET '
			requete=requete+" "+site+' = "'+str(price)
			requete=requete+'" WHERE idCard='+str(self.idCard)+";"		
			#print requete
			cursor.execute(requete)
			conn.commit()
					
				
		except Exception as e:
			print("Erreur")
		    	print e
	def verif(self):
		try:
			conn = mysql.connector.connect(host="localhost",user="root",password="magicpswd", database="magic")
			cursor = conn.cursor()
			cursor.execute("""SELECT   idCard FROM cardPrice""")
			rows = cursor.fetchall()
			flagId=False
			for i in rows:
				#print i[0]
				if i[0]==self.idCard:
					flagId=True
			if flagId==False:
				cursor = conn.cursor()
				requete="INSERT INTO cardPrice(idCard) VALUES ("+str(self.idCard)+");"
				#print requete
				cursor.execute(requete)
				conn.commit()
		except Exception as e:
			print("Erreur")
		    	print e

	def display(self):
		for i in self.priceList:
			print i[0][1:],
			print " ",
			print i[1]

	def toCSV(self):
		stringReturn=""
		for i in self.priceList:
			stringReturn=stringReturn+str(i[0][1:])+'; '
			stringReturn=stringReturn+str(i[1])+'\n'

		return stringReturn
			

			
			
