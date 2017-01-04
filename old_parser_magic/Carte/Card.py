#!/usr/bin/env python
# -*- coding: utf-8 -*-

import mysql.connector 

class Card():
	def __init__(self):
		self.idCard=0
		self.nameVO=""
		self.nameVF=""
		self.idEdition=0
		self.num=0
		self.cout=""
		self.cost=""
		self.typeVO=""
		self.typeVF=""
		self.force=None
		self.endu=None
		self.capaVO=""
		self.capaVF=""
		self.image=""
		self.url=""
		self.color=""
		self.level=-1	# 0 commun 1 unco 2 rare 3 mythique

	def display(self):
		print "name VO : ",
		print self.nameVO
		print "name VF : ",
		print self.nameVF
		print "type VO : ",
		print self.typeVO
		print "type VF : ",
		print self.typeVF
		print "Capacite VO : ",
		print self.capaVO
		print "Capacite VF : ",
		print self.capaVF
		print str(self.force)+"/"+str(self.endu)

	def foundID(self):
		
		try:
			conn = mysql.connector.connect(host="localhost",user="root",password="magicpswd", database="magic")
			cursor = conn.cursor()
			cursor.execute("""SELECT id,nameVO,nameVF,idEdition FROM edition WHERE nameVO=%s OR nameVF=%s""",
			(self.nameVO,self.nameVF))
		except Exception as e:
			print("Erreur")
		    	print e

	def load(self,idCard):
		self.idCard=idCard
		try:
			conn = mysql.connector.connect(host="localhost",user="root",password="magicpswd", database="magic")
			cursor = conn.cursor()
			requete="SELECT * FROM card WHERE id="+str(idCard)
			cursor.execute(requete)
			rows = cursor.fetchone()			
			self.nameVO=rows[1]
			self.nameVF=rows[2]
			self.level=rows[3]
			self.color=rows[4]
			self.idEdition=rows[5]
			self.typeV0=rows[6]
			self.typeVF=rows[7]
			self.cost=rows[8]			
			self.force=rows[9]
			self.endu=rows[10]
			self.capaVO=rows[11]
			self.capaVF=rows[12]
			self.image=rows[13]
			self.url=rows[14]
			
			
				
		except Exception as e:
			print("Erreur")
		    	print e

	def update(self):
		try:
			conn = mysql.connector.connect(host="localhost",user="root",password="magicpswd", database="magic")
			cursor = conn.cursor()
			requete="UPDATE card SET "
			requete=requete+"nameVO='"+self.nameVO
			requete=requete+"', nameVF='"+self.nameVF
			requete=requete+"', level="+str(self.level)
			requete=requete+", color='"+self.color
			requete=requete+"', idEdition="+str(self.idEdition)
			requete=requete+", typeVO='"+self.typeVO
			requete=requete+", typeVF='"+self.typeVF
			requete=requete+"', cout='"+str(self.cost)
			requete=requete+"', strong="+str(self.force)
			requete=requete+", endu="+str(self.endu)
			requete=requete+", capaciteVO='"+str(self.capaVO)
			requete=requete+", capaciteVF='"+str(self.capaVF)
			requete=requete+"', image='"+self.image
			requete=requete+"', url='"+self.url+"'"
			requete=requete+" WHERE id="+str(self.idCard)+";"
			#print requete
			cursor.execute(requete)
			conn.commit()
					
				
		except Exception as e:
			print("Erreur")
		    	print e

	def setEdition(self,name):
		try:
			conn = mysql.connector.connect(host="localhost",user="root",password="magicpswd", database="magic")
			cursor = conn.cursor()
			cursor.execute("""SELECT   id,name FROM     edition""")
			rows = cursor.fetchall()				
			
			for i in rows:
				if name== i[1]:
					self.idEdition=i[0]
			

		except Exception as e:
			print("Erreur")
			#conn.rollback()
		    	print e

	def record(self):
		value=(self.nameVO,self.nameVF,self.idEdition, self.typeVO, self.typeVF,self.cost,self.force,self.endu,
		self.capaVO, self.capaVF, self.image,self.url,self.color,self.level)
		try:
			conn = mysql.connector.connect(host="localhost",user="root",password="magicpswd", database="magic")
			cursor = conn.cursor()
			cursor.execute(""" INSERT INTO card(nameVO, nameVF, idEdition, typeVO, typeVF, cout, strong, endu, capaciteVO, capaciteVF, image,url,color,level)	VALUES (%s, %s, %s %s, %s, %s, %s, %s, %s, %s, %s, %s, %s,%s) """,value)
			conn.commit()
			

		except Exception as e:
			print("Erreur")
			#conn.rollback()
		    	print e

