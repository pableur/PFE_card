#!/usr/bin/env python
# -*- coding: utf-8 -*-

import mysql.connector
from HTMLParser import HTMLParser

import sys
from Card import *
from Site import *

class MagicCorpoPriceRider(HTMLParser):

	def __init__(self):
		HTMLParser.__init__(self)
		self.readPrice=False
		self.price=0.0
		
		self.readNameCost=False	
		self.tdCounter=0
		self.listPrice=[]
		self.nameCost=""
		self.etat=""
		self.readEtat=False
		self.name="MagicCorpo"


	def handle_starttag(self, tag, attrs):
		#print "Encountered an start tag :", tag
		if tag=='div':
			if len(attrs)>0:
				for i in attrs:
					if i[0]=='style':
						if i[1]=='width: 27%':
							self.price=True

		if tag=='tr':
			self.tdCounter=0

		if tag=='td' and self.price==True:
			self.tdCounter+=1

			if self.tdCounter==1:
				self.readNameCost=True
			else:
				self.readNameCost=False

			if self.tdCounter==2:
				self.readEtat=True
			else:
				self.readEtat=False

			if self.tdCounter==4:
				self.readPrice=True
			else:
				self.readPrice=False

												
	def handle_endtag(self, tag):

		if tag=='tbody' and self.price==True:
			self.price=False

	def handle_data(self, data):
		if self.readNameCost:
			self.nameCost=data
			self.readNameCost=False
		if self.readEtat:
			self.etat=data
			self.readEtat=False

		if self.readPrice:
			self.listPrice.append((self.nameCost,self.etat,self.extract_price(data)))
			self.readPrice=False

	def extract_price(self, value):		
		price=0.0
		price=value[0:len(value)-2]
		return float(price)

	def price_min(self,price):
		minPrice=price[0]
		for i in price:
			if i<minPrice:
				minPrice=i
		return minPrice

def loadEtat():
	try:
		conn = mysql.connector.connect(host="localhost",user="root",password="magicpswd", database="magic")
		cursor = conn.cursor()
		cursor.execute("""SELECT id,name FROM Etat""")
		rows = cursor.fetchall()				
	
		return rows

	except Exception as e:
		print("Erreur")
	    	print e

def loadEdition():
	try:
		conn = mysql.connector.connect(host="localhost",user="root",password="magicpswd", database="magic")
		cursor = conn.cursor()
		cursor.execute("""SELECT id,nameVO, nameVF FROM edition""")
		rows = cursor.fetchall()				
	
		return rows

	except Exception as e:
		print("Erreur")
	    	print e

def extractEtat(value):
	for i in allEtat:
		if i[1].lower()==value.lower():
			return i[0]
	print "Etat inconnu ",
	print value
 
def extractEdition(value):
	for element in allEdition:
		if element[1].lower()==value.lower():
			return element[0]
		if element[2].lower()==value.lower():
			return element[0]
	valueTemp=""
	for i in value.split():
		if i=="...":
			pass
		else:
			valueTemp=valueTemp+" "+i
	value=valueTemp[1:]
	

	listEditionPossible=[]
	next=False
	for i in allEdition:
		for wordOfEdition in str(i[1]).split():
			for wordOfValue in value.split():
				if wordOfEdition == wordOfValue:
					listEditionPossible.append(i)
					next=True
					break
			if next==True:
				next=False
				break
	
	nbWordInValue=len(value.split())	
	for element in listEditionPossible:
		nbWordIdentique=0
		for wordOfEdition in str(element[1]).split():
			for wordOfValue in value.split():
				if wordOfEdition==wordOfValue:
					nbWordIdentique=nbWordIdentique+1
		if nbWordIdentique==nbWordInValue:
			return element[0]
					 
	#print "Edition possible :"
	#for i in listEditionPossible:
	#	print i
	#print " "
	print "Edition inconnu ",
	print value.split()

def addPrice(idCard,idEtat,idEdition,price,boutique):
	try:
		conn = mysql.connector.connect(host="localhost",user="root",password="magicpswd", database="magic")
		cursor = conn.cursor()

		requete="INSERT INTO Price(idCard,idEtat,idEdition,price,boutique) VALUES ("
		requete=requete+str(idCard)+","+str(idEtat)+","+str(idEdition)+","+str(price)+',"'+boutique+'");'
		print requete
		cursor.execute(requete)
		conn.commit()				
				
	except Exception as e:
		print "Erreur : ",
	    	print e

def UpdatePrice(Card):
	print "update price : ",
	print Card.url
	mon_fichier.write('update price : '+Card.url+'\n')
	parser=MagicCorpoPriceRider()
	site=Site(Card.url)
	parser.feed(site.html)
	
	listeRecorded=[]
	for element in parser.listPrice:
		idEtat=extractEtat(element[1])
		idEdition=extractEdition(element[0])
		recorded=True
		for i in listeRecorded:
			if i[2]==idEdition and i[3]==element[2]:
				recorded=False
		if recorded==True:
			if idEtat != None and idEdition != None:
				addPrice(Card.idCard,idEtat,idEdition,element[2],parser.name)
				listeRecorded.append([Card.idCard,idEtat,idEdition,element[2],parser.name])
		
allEtat=loadEtat()
allEdition=loadEdition()


if __name__ == "__main__":
	mon_fichier = open("fichier.txt", "a")
	card=Card()
	idCard=sys.argv[1]
	card.load(idCard)
	UpdatePrice(card) 
	temp='card '+idCard+'\n'
	mon_fichier.write(temp)
	mon_fichier.close()

