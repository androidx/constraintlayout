## MotionLayout Views Sample
Sample project that demonstrates the use of MotionLayout in RecycleView.

## Overview

Using ConstraintLayout or MotionLayout in Recycler view can be tricky. 
The basic pattern of usage is 
1. Create an base layout (say res/layout/activity_main.xml) that contains <RecyclerView..>
2. Create a layout for one item (say res/layout/motion_item.xml)
1. create a subclass of RecyclerView.Adapter say ```static class CustomAdapter extends RecyclerView.Adapter<CustomViewHolder>```
1. create a subclass of RecyclerView.ViewHolder say ```static class CustomViewHolder extends RecyclerView.ViewHolder```
1. find the RecyclerView. ```rv = findViewById(R.id.recyclerView)```
1. set your custom ViewHolder to it ```rv.setAdapter(new CustomAdapter(...));```
1. set a Layout Manager on the RecyclerView ```rv.setLayoutManager(new LinearLayoutManager(this));```

-----------------------------
 

## Contributing

If you'd like to get involved and contribute please read [CONTRIBUTING](https://github.com/androidx/constraintlayout/blob/main/CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Authors

- **John Hoford** ([jafu888](https://github.com/jafu888))

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](https://github.com/androidx/constraintlayout/blob/main/LICENSE) file for details
