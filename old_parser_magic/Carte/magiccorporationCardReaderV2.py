#!/usr/bin/env python
# -*- coding: utf-8 -*-

import mysql.connector 

from sgmllib import SGMLParser
from HTMLParser import HTMLParser
import urllib2
import os
import re
from Site import *
from Card import *
from Edition import *
from Price import *

url="http://www.magiccorporation.com/gathering-cartes-edition-170-tenebres-sur-innistrad.html"
 
def findColor(url):
	if url=='/images/magic/couleurs/micro/incolor.gif':
		return 'incolor'
	elif url=='/images/magic/couleurs/micro/white.gif':
		return 'white'
	elif url=='/images/magic/couleurs/micro/blue.gif':
		return 'blue'
	elif url=='/images/magic/couleurs/micro/red.gif':
		return 'red'
	elif url=='/images/magic/couleurs/micro/green.gif':
		return 'green'
	elif url=='/images/magic/couleurs/micro/black.gif':
		return 'black'
	elif url=='/images/magic/couleurs/micro/multicolor.gif':
		return 'multicolor'
	return ''

def findLevel(url):
	if url=='/images/magic/rarete/icon/rare.gif':
		return 2
	elif url=='/images/magic/rarete/icon/common.gif':
		return 0
	elif url=='/images/magic/rarete/icon/uncommon.gif':
		return 1
	elif url=='/images/magic/rarete/icon/mystic_rare.gif':
		return 3

def findCost(value):
	cost=""
	for i in value:
		cost=cost+i[len(i)-5]
	return cost	

class CardParser(HTMLParser):

	def __init__(self,idCard):
		HTMLParser.__init__(self)
		self.html_table_editions=False
		self.image=False
		self.caracteristiqueVO=False
		self.caracteristiqueVF=False
		self.readPrice=False
		self.price=0.0
		self.card=Card()
		self.card.load(idCard)
		self.card.capaVO=""
		self.card.capaVF=""

		self.text=False
		self.readNameCost=False		
		self.readCost=False
		self.readCapa=False
		self.endCost=False
		self.imgCost=[]
		self.brCounter=0
		self.tdCounter=0
		self.listPrice=[]
		self.nameCost=""
		self.read=False

		self.ligneStartAnglais=None
		self.ligneStartFrancais=None


	def handle_starttag(self, tag, attrs):
		#print "Encountered an start tag :", tag
		if tag=='div':
			if len(attrs)>0:
				if self.caracteristiqueVO==True and attrs[0][0]=='class' and attrs[0][1]=='block_content':
					self.readCost=True
				for i in attrs:
					if i[0]=='style':
						if i[1]=='width: 225px':
							self.image=True
							self.text=False
							self.price=False
						elif i[1]=='width: 30%':
							self.image=False
							self.text=True
							self.price=False
						elif i[1]=='width: 27%':
							self.image=False
							self.text=False
							self.price=True
						
		if tag=='img' and self.readCost==True and self.endCost==False:
			self.imgCost.append(attrs[0][1]);

		# Enregistre l'image 
		if tag=='img' and self.image==True:
			self.image=False
			for i in attrs:
				if i[0]=='src':
					self.card.image=i[1]

		# Recupere les informations de prix
		if self.price:
			if tag=='tr':
				self.tdCounter=0
				self.nameCost=""
			if tag=='td' and self.price==True:
				self.tdCounter+=1

				if self.tdCounter==1 or self.tdCounter==2 or self.tdCounter==3:
					self.readNameCost=True
				else:
					self.readNameCost=False

				if self.tdCounter==4:
					self.readPrice=True
				else:
					self.readPrice=False

		if tag=='br' or tag=='b' and self.text:
			self.brCounter+=1
												
	def handle_endtag(self, tag):
	        #print "Encountered an end tag :", tag
		if tag=='br' and self.readCost==True:
			self.readCost=False
			self.endCost=True
			self.readCapa=True
		if tag=='div' and self.readCapa==True:
			self.caracteristiqueVO=False
			self.card.cost=findCost(self.imgCost)

		if tag=='html':
			self.card.display()

	def handle_data(self, data):
		for i in data :
			if ord(i)==9 or ord(i)==10:
			 	data = data.replace(i,"")
		if len(data)==1 and ord(data[0])==0x20:
			data = data.replace(data[0],"")
		if len(data)>0:
			if self.text:
				if self.ligneStartAnglais==None and data=="Texte Anglais":
					self.ligneStartAnglais=self.brCounter
				if self.ligneStartFrancais==None and data[0:10]=="Texte Fran":
					self.ligneStartFrancais=self.brCounter

			if self.ligneStartAnglais != None:			
				if self.text and self.brCounter==(self.ligneStartAnglais+1):
					self.card.nameVO=data
				if self.text and self.brCounter==(self.ligneStartAnglais+2):
					self.card.typeVO=data
				if self.text and self.brCounter>=(self.ligneStartAnglais+3) and self.ligneStartFrancais==None:
					print "capa "+self.card.capaVO					
					self.card.capaVO=self.card.capaVO+data
					print data

			if self.ligneStartFrancais != None:
				if self.text and self.brCounter==(self.ligneStartFrancais+1):
					self.card.nameVF=data			
				if self.text and self.brCounter==(self.ligneStartFrancais+2):
					self.card.typeVF=data
				if self.text and self.brCounter>=(self.ligneStartFrancais+3):
					print "capa "+self.card.capaVF					
					self.card.capaVF=" "+self.card.capaVF+" "+data+" "

			if self.readNameCost:
				self.nameCost=self.nameCost+" "+data
				self.readNameCost=False

			if self.readPrice:
				self.listPrice.append((self.nameCost,self.extract_price(data)))
				self.readPrice=False

			if self.text:
				#print str(self.brCounter)+" "+(data)
				"""for i in data :
					print ord(i), 
				print " "
"""

	def extract_price(self, value):		
		price=0.0
		price=value[0:len(value)-2]
		return float(price)

	def extract_name(self, value):		
		print value
		return None

	def price_min(self,price):
		minPrice=price[0]
		for i in price:
			if i<minPrice:
				minPrice=i
		return minPrice

if __name__ == "__main__":
	print "////////////////////////////////////////////////////////////"
	print "///	Lit les cartes et les enregistres dans la BDD	///"
	print "////////////////////////////////////////////////////////////"
	
	"""
	edition=Edition()
	allEdition=edition.all()
	for temp in allEdition:
		parser = EditionParser(temp[0])
		urlSite='http://www.magiccorporation.com/'+temp[1]
		print urlSite
		site=Site(urlSite)
		parser.feed(site.html)
	"""
	
	parser=CardParser(6)
	site=Site("http://www.magiccorporation.com/gathering-cartes-view-29824-calciderm.html")
	parser.feed(site.html)
	"""
	try:
		conn = mysql.connector.connect(host="localhost",user="root",password="magicpswd", database="magic")
		cursor = conn.cursor()
		cursor.execute("SELECT id, url FROM card")
		rows = cursor.fetchall()
			
		for i in rows:
			print i[0],
			print i[1]
			try:
				parser=CardParser(i[0])
				site=Site(i[1])
				parser.feed(site.html)
			except Exception as e:
				print("Erreur")
	    			print e	
				
	except Exception as e:
		print("Erreur")
	    	print e	
	"""
