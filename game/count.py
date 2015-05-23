import csv

score={}
for i in range(1, 9):
    temp=[]
    score[i]=temp
size=15
for i in range(1,size+1):
    csvfile=file('logdata/datacsv/'+str(i)+'.csv','rb')
    reader=csv.reader(csvfile)
    j=-1
    for line in reader:
        if j==-1:
            j+=1
            continue
        if '-' in line[6]:
            continue
        score[int(line[0][0])].append(int(line[6]))
        j+=1
        #print line[6]
    csvfile.close()
for key,user in score.items():
    ans=0.0
    for s in user:
        ans+=s
    print key,'\'s ave  : %.2f' % (ans/len(user)),' data :',user