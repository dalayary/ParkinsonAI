from keras import backend as K
from sklearn.model_selection import train_test_split
from keras.utils import to_categorical
import numpy as np, pandas as pd, io, csv

# Classifier Lib
from sklearn import svm
from sklearn.svm import SVC
from sklearn.metrics import confusion_matrix
from sklearn.metrics import accuracy_score
from sklearn.neighbors import KNeighborsClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.ensemble import RandomForestClassifier
from sklearn.neural_network import MLPClassifier
from sklearn.linear_model import SGDClassifier
from sklearn.pipeline import Pipeline
from sklearn.metrics import classification_report

##########################################################################################################################################

filename = '../Main/ds_results.csv'
data = pd.read_csv(filename, header=1)

# Load  DataSet
Fs = data.iloc[:, 1:13]

# Load Lables
target = data.iloc[:, 0]

# Reshape and Split data to Train data and Test data
arrayofdata_ = np.array(Fs)

labels = np.array(target)

one_hot_labels = to_categorical(labels, num_classes=2)

x_train, x_test, y_train, y_test = train_test_split(arrayofdata_,
                                                    one_hot_labels,
                                                    test_size=0.2, shuffle=True,
                                                    random_state=42)

x_train = np.reshape(x_train, (x_train.shape[0], x_train.shape[1], 1))
x_test = np.reshape(x_test, (x_test.shape[0], x_train.shape[1], 1))

# Reshape Data for MachinLearning Classifier
x_train1 = np.reshape(x_train, (x_train.shape[0], x_train.shape[1] * x_train.shape[2]))
x_test1 = np.reshape(x_test, (x_test.shape[0], x_test.shape[1] * x_test.shape[2]))
y_train1 = np.argmax(y_train, axis=1)
y_test1 = np.argmax(y_test, axis=1)

# HC:Healthy Control Adn PD:Parkinson Disease
my_tags = ['HC', 'PD']

# SVM Classifier
svm_clf = svm.SVC(kernel='linear')
svm_clf.fit(x_train1, y_train1)
svm_ped = svm_clf.predict(x_test1)
print('SVM accuracy is %s' % accuracy_score(svm_ped, y_test1))
print(classification_report(y_test1, svm_ped, target_names=my_tags))

# SGD Classifier
my_tags = ['HC', 'PD']
from sklearn.linear_model import SGDClassifier

sgd = Pipeline([
    ('clf', SGDClassifier(loss='hinge', penalty='l2', alpha=1e-3, random_state=42, max_iter=5, tol=None)),
])
sgd.fit(x_train1, y_train1)

y_pred = sgd.predict(x_test1)
print('SGD accuracy is %s' % accuracy_score(y_pred, y_test1))
print(classification_report(y_test1, y_pred, target_names=my_tags))

# LR Classifier
from sklearn.linear_model import LogisticRegression

logreg = Pipeline([('clf', LogisticRegression(n_jobs=1, C=1e5)), ])
logreg.fit(x_train1, y_train1)
y_pred_lr = logreg.predict(x_test1)
print('LR accuracy is %s' % accuracy_score(y_pred_lr, y_test1))
print(classification_report(y_test1, y_pred_lr, target_names=my_tags))

# K-Nearest Neighbours Classifier


clf = KNeighborsClassifier(n_neighbors=1)
clf.fit(x_train1, y_train1)
y_pred_Knn = clf.predict(x_test1)
cm = confusion_matrix(y_test1, y_pred_Knn)
print('KNN accuracy is %s' % accuracy_score(y_pred_Knn, y_test1))
print(classification_report(y_test1, y_pred_Knn, target_names=my_tags))

# RandomForest Classifier

RF = RandomForestClassifier(n_estimators=1000, max_depth=10, random_state=0).fit(x_train1, y_train1)
y_pred_rf = RF.predict(x_test1)
print('RF accuracy is %s' % accuracy_score(y_pred_rf, y_test1))
print(classification_report(y_test1, y_pred_rf, target_names=my_tags))

# MLP Classifier
mlp = MLPClassifier(solver='adam', max_iter=1000, alpha=1e-5, hidden_layer_sizes=(150, 10), random_state=1).fit(
    x_train1, y_train1)
y_pred_mlp = mlp.predict(x_test1)
print('MLP accuracy is %s' % accuracy_score(y_pred_mlp, y_test1))
print(classification_report(y_test1, y_pred_mlp, target_names=my_tags))
