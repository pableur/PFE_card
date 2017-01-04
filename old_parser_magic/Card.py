import os

class Card:
	def __init__(self, name):
		self.name=name
		self.image=""
		self.prix=0.0
		self.capacite=""
		self.extension="None"
		
	def save(self, path):
		if path[len(path)-1]!='/':
			path=path+'/'
		path=path+self.extension+".txt"
		
		file=open(path,"a");
		buffer=self.name+","+str(self.prix)+","+self.extension+","+self.description+"\n"
		file.write(buffer);