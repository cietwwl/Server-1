# -*- coding: utf-8 -*-
import os,os.path,sys,time
from tkFileDialog import askopenfilename
import Tkinter as tk

def export():
	proto_file=askopenfilename()
	_,proto_file=os.path.split(proto_file)
	os.system('one.bat %s'%(proto_file))
	#processXls(proto_file)
	print proto_file

if __name__=='__main__':
	m_window=tk.Tk()
	m_window.title("fuck proto")
	m_window.geometry("600x400")
	content = tk.Frame(m_window)
	content.grid(row=0,column=0)
	opMsg=tk.StringVar()
	msg_lbl=tk.Label(content,textvariable=opMsg)
	opMsg.set("ready!!!\n%s\n"%time.strftime('%d/%b/%Y:%x'))
	open_btn=tk.Button(content,text=u"导出单个文件",command=export)
	open_btn.grid(row=1,column=1)
	m_window.mainloop()
