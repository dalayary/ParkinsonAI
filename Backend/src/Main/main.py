import flask
import werkzeug
from keras.utils import to_categorical

from Main.ml import *

app = flask.Flask(__name__)


@app.route('/', methods=['GET', 'POST'])
def handle_request():
    files_ids = list(flask.request.files)
    print("\nNumber of Received Audios : ", len(files_ids))
    for file_id in files_ids:
        audiofile = flask.request.files[file_id]
        filename = werkzeug.utils.secure_filename(audiofile.filename)
        print("Audio Filename : " + audiofile.filename)
        audiofile.save(filename)

    path = '../Model/modelMlp.sav'
    model = loadModel(path)
    wavPath = 'RecVoiceParkinson.wav'
    x_wav = np.fromfile(wavPath, dtype='int16')

    res = predict(model, x_wav)
    print(res)
    return str(res)


@app.route('/acc', methods=['GET', 'POST'])
def acc():
    # load library
    from sklearn.model_selection import train_test_split
    from sklearn.metrics import accuracy_score
    from sklearn.neural_network import MLPClassifier

    filename = './ds_results.csv'
    # Load  DataSet
    data = pd.read_csv(filename, header=1)  # Loading CSV dataset

    # select the row and column
    # using implicit indexing
    Fs = data.iloc[:, 1:13]

    # Load Lables
    target = data.iloc[:, 0]

    # Reshape into array
    arrayofdata_ = np.array(Fs)

    labels = np.array(target)

    # find out index with the largest element in the matrix
    one_hot_labels = to_categorical(labels, num_classes=2)

    # Split data to Train data and Test data
    x_train, x_test, y_train, y_test = train_test_split(arrayofdata_,
                                                        one_hot_labels,
                                                        test_size=0.20, shuffle=True,
                                                        random_state=42)
    # reshape array
    x_train = np.reshape(x_train, (x_train.shape[0], x_train.shape[1], 1))
    x_test = np.reshape(x_test, (x_test.shape[0], x_train.shape[1], 1))

    # Reshape Data for MachinLearning Classifier
    x_train1 = np.reshape(x_train, (x_train.shape[0], x_train.shape[1] * x_train.shape[2]))
    x_test1 = np.reshape(x_test, (x_test.shape[0], x_test.shape[1] * x_test.shape[2]))
    y_train1 = np.argmax(y_train, axis=1)
    y_test1 = np.argmax(y_test, axis=1)

    # trains iterativley through the model
    mlp = MLPClassifier(solver='adam', max_iter=1000, alpha=1e-5, hidden_layer_sizes=(150, 5), random_state=1).fit(
        x_train1,
        y_train1)
    # prediction
    y_pred_mlp = mlp.predict(x_test1)
    print('MLP accuracy is %s' % accuracy_score(y_pred_mlp, y_test1))
    # return accurancy result
    return str('%0.2f' % (100 * accuracy_score(y_pred_mlp, y_test1))) + "%"


# Emulator
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)

