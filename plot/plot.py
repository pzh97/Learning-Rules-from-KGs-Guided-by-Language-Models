import pandas as pd
import matplotlib.pyplot as plt
import argparse

parser = argparse.ArgumentParser(description='Process some arguments.')
parser.add_argument('--w', type=str, help='specify working directory', required=True)
parser.add_argument('--f', type=str, help='specify filename', required=True)
parser.add_argument('--n', type=str, help='specify the name of the plot', required=True)
args = parser.parse_args()

data = pd.read_csv("./"+args.w+"/"+args.f)
df = pd.DataFrame(data)

top_5 = list(df.iloc[0,1:12])
top_10 = list(df.iloc[1,1:12])
top_20 = list(df.iloc[2,1:12])
top_50 = list(df.iloc[3,1:12])
top_100 = list(df.iloc[4,1:12])

mu_init = []
for col in data.columns:
    mu_init.append(col)
mu = mu_init[1:]

plt.figure(figsize=(8,6))

plt.plot(mu, top_5, marker = 'o', label = 'top5')
plt.plot(mu, top_10, marker = 'x', label = 'top10')
plt.plot(mu, top_20, marker = 's', label = 'top20')
plt.plot(mu, top_50, marker = '*', label = 'top50')
plt.plot(mu, top_100, marker = 'd', label = 'top100')

plt.grid()

plt.legend(loc="lower left")
plt.xlabel(r"$\lambda$")
plt.ylabel("Average Predicted Precision")
plt.title(args.n)
plt.show()
