# Style Transfer

## Preprocessing Training Data

```bash
python create_training_dataset.py --output test\training_images.tfrecord --image-dir data\train2014
```



## Training a Style Transfer Model

```bash
python style_transfer\train.py \
--training-image-dset test\training_images.tfrecord \
--style-images test\style_images\starry_night.jpg \
--model-checkpoint test\starry_night_test.h5 \
--image-size 256,256 \
--alpha 0.25 \
--num-iterations 5000
```



## Stylizing Images

To stylize an image with a trained model you can run:

```bash
python stylize_image.py \
--input-image test/dog.jpg \
--output-image test/test_dog.jpg \
--model-checkpoint test/starry_night_test.h5


```

### Test result:

Style image:
![image](https://github.com/sustech-se2019/main-project-repository-yoha/blob/master/style_transfer_model/test/style_images/starry_night.jpg)


Input:

![image](https://github.com/sustech-se2019/main-project-repository-yoha/blob/master/style_transfer_model/test/dog.jpg)

Output:

![image](https://github.com/sustech-se2019/main-project-repository-yoha/blob/master/style_transfer_model/test/test_dog1.jpg)



Input:

![image](https://github.com/sustech-se2019/main-project-repository-yoha/blob/master/style_transfer_model/test/woman.jpg)

Output:

![image](https://github.com/sustech-se2019/main-project-repository-yoha/blob/master/style_transfer_model/test/test_woman1.jpg)

The test result is good. No matter what the size of input image is, the output image is 256×256 and stylized.



## Convert to Mobile

### Convert to TensorFlow Mobile

Models cannot be converted to TFLite because some operations are not supported, but TensorFlow Mobile works fine. To convert your model to an optimized frozen graph, run:

```bash
python convert_to_tfmobile.py \
--keras-checkpoint example/starry_night_test.h5 \
--alpha 0.25 \
--image-size 640,480 \
--output-dir test/
```

This produces a number of Tensorflow graph formats. The `*_optimized.pb` graph file is the one you want to use with your app. Note that the input node name is `input_1` and the output node name is `deprocess_stylized_image_1/mul`.
