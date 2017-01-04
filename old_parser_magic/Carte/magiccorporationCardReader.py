#!/usr/bin/env python
# -*- coding: utf-8 -*-

import mysql.connector 

from sgmllib import SGMLParser
from HTMLParser import HTMLParser
import urllib2
import os
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
		self.card.capa=""

		self.readNameCost=False		
		self.readCost=False
		self.readCapa=False
		self.endCost=False
		self.imgCost=[]
		self.brCounter=0
		self.tdCounter=0
		self.listPrice=[]
		self.nameCost=""


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
						if i[1]=='width: 30%':
							self.caracteristiqueVO=True
						if i[1]=='width: 27%':
							self.price=True
						
		if tag=='img' and self.readCost==True and self.endCost==False:
			self.imgCost.append(attrs[0][1]);

		if tag=='img' and self.image==True:
			self.image=False
			for i in attrs:
				if i[0]=='src':
					self.card.image=i[1]
					#print i[1]

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

		if tag=='br' and self.caracteristiqueVO:
			self.brCounter+=1
												
	def handle_endtag(self, tag):
	        #print "Encountered an end tag :", tag
		if tag=='tbody' and self.price==True:
			self.price=False
		if tag=='br' and self.readCost==True:
			self.readCost=False
			self.endCost=True
			self.readCapa=True
		if tag=='div' and self.readCapa==True:
			self.caracteristiqueVO=False
			self.card.cost=findCost(self.imgCost)

		if tag=='html':
			priceTest = Price(self.card.idCard,self.listPrice)
			priceTest.update("magicCorpo",priceTest.toCSV())
			#print "price mini ",
			#print self.listPrice
			self.card.update()

	def handle_data(self, data):
		if self.caracteristiqueVO and self.brCounter==1:
			#print data
			self.card.type=data
		if self.caracteristiqueVO and self.brCounter>1 and self.brCounter<4:
			#print data
			self.card.capa=self.card.capa+" "+data

		if self.readNameCost:
			self.nameCost=self.nameCost+" "+data
			self.readNameCost=False

		if self.readPrice:
			#print self.nameCost
			self.listPrice.append((self.nameCost,self.extract_price(data)))
			self.readPrice=False

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

class EditionParser(HTMLParser):

	def __init__(self,idEdition):
		HTMLParser.__init__(self)
		self.html_table_editions=False
		self.tdCounter=0
		self.card=Card()
		self.card.idEdition=idEdition

	def handle_starttag(self, tag, attrs):
		#print "Encountered an start tag :", tag
		if tag=='table':
			if attrs[3][1]=="html_table editions":
				self.html_table_editions=True
		if self.html_table_editions==True:
			if tag=="td":
				self.tdCounter=self.tdCounter+1

			if tag=="img" and self.tdCounter==1:
					self.card.color=findColor(attrs[0][1])
			if tag=="img" and self.tdCounter==2:
					self.card.level=findLevel(attrs[0][1])
			if tag=="a" and self.tdCounter==4:
					self.card.url='http://www.magiccorporation.com/'+attrs[0][1]
												
	def handle_endtag(self, tag):
	        #print "Encountered an end tag :", tag
		if tag=='table' and self.html_table_editions==True:
			self.html_table_editions=False
		
		if self.html_table_editions:
			if tag=='tr':
				self.tdCounter=0
				self.card.record()	
		

	def handle_data(self, data):
		#print data
		if self.html_table_editions:
			if self.tdCounter==0:
				print " "
	
			if self.tdCounter==4:
				self.card.nameVO=data
				print "Name ",
				print data,
			if self.tdCounter==5:
				self.card.nameVF=data			
			if self.tdCounter==7:
				self.card.type=data
			if self.tdCounter==8:
				strong=0
				endu=0
				position=0
				switch=False
				for i in data:
					if i=="*":
						if switch:
							endu=0
						else:
							strong=0
					elif i=="/":
						switch=True
						position=0

					else:
						try:
							if switch:
								endu=endu*10**position+int(i)
							else:
								strong=strong*10**position+int(i)
						except:
							pass
						position=position+1
				print " Force : ",
				print strong,
				print ", endu ",
				print endu,
				self.card.force=strong
				self.card.endu=endu

				
				

if __name__ == "__main__":
	print "//////////////////////////////////////////////"
	print "///	    Enregistre les editions 	  ///"
	print "//////////////////////////////////////////////"
	
	"""
	edition=Edition()
	allEdition=edition.all()
	for temp in allEdition:
		parser = EditionParser(temp[0])
		urlSite='http://www.magiccorporation.com/'+temp[1]
		print urlSite
		site=Site(urlSite)
		parser.feed(site.html)
	
	
	parser=CardParser(5)
	site=Site("http://www.magiccorporation.com/gathering-cartes-view-29823-benevolent-bodyguard.html")
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

