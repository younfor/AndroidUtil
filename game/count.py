import csv
import sys
score = {}
for i in range(1, 9):
    temp = []
    score[i] = temp
size = (int)(sys.argv[1])
print 'total:', size
for i in range(1, size + 1):
    csvfile = file('logdata/datacsv/' + str(i) + '.csv', 'rb')
    reader = csv.reader(csvfile)
    j = -1
    for line in reader:
        if j == -1:
            j += 1
            continue
        if '-' in line[6]:
            continue
        score[int(line[0][0])].append(int(line[6]))
        j += 1
        # print line[6]
    csvfile.close()
for key, user in score.items():
    ans = 0
    i=0
    total=0.0
    result=[]
    for s in user:
        ans += s
        total+=s
        i+=1
        if i%3==0:
            result.append(ans)
            ans=0
    print key, '\'s ave  : %.2f' % (total / len(user)), ' data :', user,'rank: ',result