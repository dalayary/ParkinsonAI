# Main Modules

import itertools
from itertools import cycle
from statistics import mean

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from keras import backend as K
from keras.layers import Dense, Conv1D, MaxPooling1D, Dropout, Flatten
from keras.layers.normalization import BatchNormalization
from keras.models import Sequential
from keras.optimizers import Adam
from keras.utils import to_categorical
##K-fold Modules
from keras.wrappers.scikit_learn import KerasClassifier
## Precision/Recall/F1_score AND Roc curve Modules
from scipy import interp
from sklearn.metrics import average_precision_score
from sklearn.metrics import confusion_matrix
from sklearn.metrics import precision_recall_curve
from sklearn.metrics import roc_curve, auc
from sklearn.model_selection import KFold
from sklearn.model_selection import cross_val_score
from sklearn.model_selection import train_test_split
##compute_class_weight Modules
from sklearn.utils import compute_class_weight

# confusion_matrix Modules

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

# Reshape Data for Confusion_matrix
x_train1 = np.reshape(x_train, (x_train.shape[0], x_train.shape[1] * x_train.shape[2]))
x_test1 = np.reshape(x_test, (x_test.shape[0], x_test.shape[1] * x_test.shape[2]))
y_train1 = np.argmax(y_train, axis=1)
y_test1 = np.argmax(y_test, axis=1)


# Convolution1D network

def NetworkCNN():
    model = Sequential()
    model.add(Conv1D(128, 4, padding='same', activation='relu', input_shape=(x_train.shape[1], 1)))
    model.add(BatchNormalization())
    model.add(Conv1D(128, 4, padding='same', activation='relu'))
    model.add(BatchNormalization())
    model.add(MaxPooling1D(pool_size=(1), strides=2))

    model.add(Conv1D(64, 4, padding='same', activation='relu'))
    model.add(BatchNormalization())
    model.add(Conv1D(64, 4, padding='same', activation='relu'))
    model.add(BatchNormalization())
    model.add(MaxPooling1D(pool_size=1, strides=2))

    model.add(Conv1D(32, 4, padding='same', activation='relu'))
    model.add(BatchNormalization())
    model.add(Conv1D(32, 4, padding='same', activation='relu'))
    model.add(BatchNormalization())
    model.add(MaxPooling1D(pool_size=1, strides=2))

    model.add(Conv1D(16, 4, padding='same', activation='relu'))
    model.add(BatchNormalization())
    model.add(Conv1D(16, 4, padding='same', activation='relu'))
    model.add(BatchNormalization())
    model.add(MaxPooling1D(pool_size=1, strides=2))

    model.add(Conv1D(8, 4, padding='same', activation='relu'))
    model.add(BatchNormalization())
    model.add(Conv1D(8, 4, padding='same', activation='relu'))
    model.add(BatchNormalization())
    model.add(MaxPooling1D(pool_size=1, strides=2))

    model.add(Flatten())

    model.add(Dense(16, activation='relu'))
    model.add(Dropout(0.3))

    model.add(Dense(2, activation='softmax'))

    model.compile(Adam(lr=0.0001), loss="categorical_crossentropy", metrics=["accuracy"])

    # Compute Input weights

    y_integers = np.argmax(one_hot_labels, axis=1)
    class_weights = compute_class_weight('balanced', np.unique(y_integers), y_integers)
    d_class_weights = dict(enumerate(class_weights))

    history = model.fit(x_train, y_train,

                        class_weight=d_class_weights,
                        batch_size=16, shuffle=True, validation_split=0.1,
                        validation_data=(x_test, y_test), verbose=2,
                        epochs=1000)

    print("Accuracy is:", mean(history.history['accuracy']))
    print("Val Accuracy is:", mean(history.history['val_accuracy']))
    print("Loss is:", mean(history.history['loss']))
    print("Val Loss is:", mean(history.history['val_loss']))

    # Model Loss Plot
    plt.plot(history.history['loss'], linewidth=2, label='Train')
    plt.plot(history.history['val_loss'], linewidth=2, label='Test')
    plt.legend(loc='upper right')
    plt.title('Model loss')
    plt.ylabel('Loss')
    plt.xlabel('Epoch')
    plt.show()

    # Model Accuracy Plot

    plt.plot(history.history['accuracy'])
    plt.plot(history.history['accuracy'])
    plt.title('model accuracy')
    plt.ylabel('accuracy')
    plt.xlabel('epoch')
    plt.legend(['train', 'validation'], loc='upper left')

    return model


#################################################Confusion_matrix Code#########################################

Net = NetworkCNN()
Net = Net.predict(x_test)
classification = np.argmax(Net)
y_pred_2 = Net.argmax(axis=-1)

class_names = ["Healthy", "Parkinson"]


def plot_confusion_matrix(cm, classes,
                          normalize=False,
                          title='Confusion matrix',
                          cmap=plt.cm.Blues):
    """
  This function prints and plots the confusion matrix.
  Normalization can be applied by setting `normalize=True`.
  """
    if normalize:
        cm = cm.astype('float') / cm.sum(axis=1)[:, np.newaxis]
        print("Normalized confusion matrix")
    else:
        print('Confusion matrix, without normalization')

    print(cm)

    plt.imshow(cm, interpolation='nearest', cmap=cmap)
    plt.title(title)
    plt.colorbar()
    tick_marks = np.arange(len(classes))
    plt.xticks(tick_marks, classes, rotation=45)
    plt.yticks(tick_marks, classes)

    fmt = '.2f' if normalize else 'd'
    thresh = cm.max() / 2.
    for i, j in itertools.product(range(cm.shape[0]), range(cm.shape[1])):
        plt.text(j, i, format(cm[i, j], fmt),
                 horizontalalignment="center",
                 color="white" if cm[i, j] > thresh else "black")

    plt.ylabel('True label')
    plt.xlabel('Predicted label')
    plt.tight_layout()


cnf_matrix = confusion_matrix(y_test1, y_pred_2)
np.set_printoptions(precision=2)

# Plot non-normalized confusion matrix
plt.figure()
plot_confusion_matrix(cnf_matrix, classes=class_names,
                      title='Confusion matrix, without normalization')
plt.show()

#################################################Cross-Validation with k-Fold=3#########################################


arrayofdata_ = np.reshape(arrayofdata_, (arrayofdata_.shape[0], arrayofdata_.shape[1], 1))
estimator = KerasClassifier(build_fn=NetworkCNN, epochs=1000, batch_size=16, verbose=0)
kfold = KFold(n_splits=3, shuffle=True, random_state=42)
results = cross_val_score(estimator, arrayofdata_, one_hot_labels, cv=kfold)
print("Baseline: %.2f%% (%.2f%%)" % (results.mean() * 100, results.std() * 100))

#################################################Precision/Recall/F1_score AND Roc curve#########################################


Net2 = NetworkCNN()
y_score = Net2.predict(x_test)

# For each class
precision = dict()
recall = dict()
average_precision = dict()
for i in range(3):
    precision[i], recall[i], _ = precision_recall_curve(y_test[i],
                                                        y_score[i])
    average_precision[i] = average_precision_score(y_test[i], y_score[i])

# A "micro-average": quantifying score on all classes jointly
precision["micro"], recall["micro"], _ = precision_recall_curve(y_test.ravel(),
                                                                y_score.ravel())
average_precision["micro"] = average_precision_score(y_test, y_score,
                                                     average="micro")
print('Average precision score, micro-averaged over all classes: {0:0.2f}'
      .format(average_precision["micro"]))

precision_ = mean(precision["micro"])
recall_ = mean(recall["micro"])
f_Score = 2 * ((precision_ * recall_) / (precision_ + recall_ + K.epsilon()))

print('precision is:')
print(precision_)
print('recall is:')
print(recall_)
print('f_Score is:')
print(f_Score)

n_classes = 2

# Plot linewidth.
lw = 2

# Compute ROC curve and ROC area for each class
fpr = dict()
tpr = dict()
roc_auc = dict()
for i in range(n_classes):
    fpr[i], tpr[i], _ = roc_curve(y_test[:, i], y_score[:, i])
    roc_auc[i] = auc(fpr[i], tpr[i])

# Compute micro-average ROC curve and ROC area
fpr["micro"], tpr["micro"], _ = roc_curve(y_test.ravel(), y_score.ravel())
roc_auc["micro"] = auc(fpr["micro"], tpr["micro"])

# Compute macro-average ROC curve and ROC area

# First aggregate all false positive rates
all_fpr = np.unique(np.concatenate([fpr[i] for i in range(n_classes)]))

# Then interpolate all ROC curves at this points
mean_tpr = np.zeros_like(all_fpr)
for i in range(n_classes):
    mean_tpr += interp(all_fpr, fpr[i], tpr[i])

# Finally average it and compute AUC
mean_tpr /= n_classes

fpr["macro"] = all_fpr
tpr["macro"] = mean_tpr
roc_auc["macro"] = auc(fpr["macro"], tpr["macro"])

# Plot all ROC curves
plt.figure(1)
plt.plot(fpr["micro"], tpr["micro"],
         label='micro-average ROC curve (area = {0:0.2f})'
               ''.format(roc_auc["micro"]),
         color='deeppink', linestyle=':', linewidth=4)

plt.plot(fpr["macro"], tpr["macro"],
         label='macro-average ROC curve (area = {0:0.2f})'
               ''.format(roc_auc["macro"]),
         color='navy', linestyle=':', linewidth=4)

colors = cycle(['aqua', 'darkorange', 'cornflowerblue'])
for i, color in zip(range(n_classes), colors):
    plt.plot(fpr[i], tpr[i], color=color, lw=lw,
             label='ROC curve of class {0} (area = {1:0.2f})'
                   ''.format(i, roc_auc[i]))

plt.plot([0, 1], [0, 1], 'k--', lw=lw)
plt.xlim([0.0, 1.0])
plt.ylim([0.0, 1.05])
plt.xlabel('False Positive Rate')
plt.ylabel('True Positive Rate')
plt.title('Some extension of Receiver operating characteristic to multi-class')
plt.legend(loc="lower right")
plt.show()
