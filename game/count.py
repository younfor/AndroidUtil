import csv
import sys,os
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
record = False
if len(sys.argv)==3:
    record = True
    recordfile = (str)(sys.argv[2])
    if not os.path.exists(r'result'):
        print 'create result'
        os.mkdir(r'result')
    os.system('rm result/' + recordfile + '.txt')
    output = open('result/' + recordfile + '.txt', 'w+')
for key, user in score.items():
    i = 0
    total = 0.0
    for s in user:
        total += s
        i += 1

    print key, '\'s ave  : %.2f' % (total / len(user)), ' data :', user
    a = '\'s ave  : %.2f' % (total / len(user))
    if record == True:
        output.write((str)(key) + (str)(a) + '  '+ (str)(user) + '\n')
if record == True:
    output.close()
