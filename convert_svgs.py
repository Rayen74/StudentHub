# VectorDrawable converter for Lucide Icons
import os

vectors = {
    'ic_home.xml': '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:pathData="M15,21v-8a1,1 0,0 0,-1 -1h-4a1,1 0,0 0,-1 1v8"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
    <path
        android:pathData="M3,10a2,2 0,0 1,0.709 -1.528l7,-5.999a2,2 0,0 1,2.582 0l7,5.999A2,2 0,0 1,21 10v9a2,2 0,0 1,-2 2H5a2,2 0,0 1,-2 -2z"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
</vector>''',

    'ic_profile.xml': '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:pathData="M19,21v-2a4,4 0,0 0,-4 -4H9a4,4 0,0 0,-4 4v2"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
    <path
        android:pathData="M12,11A4,4 0,1 0,12,3a4,4 0,0 0,0 8z"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
</vector>''',

    'ic_users.xml': '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:pathData="M16,21v-2a4,4 0,0 0,-4 -4H6a4,4 0,0 0,-4 4v2"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
    <path
        android:pathData="M9,11A4,4 0,1 0,9,3a4,4 0,0 0,0 8z"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
    <path
        android:pathData="M22,21v-2a4,4 0,0 0,-3 -3.87"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
    <path
        android:pathData="M16,3.13a4,4 0,0 1,0 7.75"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
</vector>''',

    'ic_settings.xml': '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:pathData="M12,15A3,3 0,1 0,12,9a3,3 0,0 0,0 6z"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
    <path
        android:pathData="M19.4,15a1.65,1.65 0,0 0,0.33 1.82l0.06,0.06a2,2 0,0 1,0 2.83,2 2 0,0 1,-2.83 0l-0.06,-0.06a1.65,1.65 0,0 0,-1.82 -0.33,1.65 1.65,0 0,0 -1,1.51V21a2,2 0,0 1,-2 2,2 2,0 0,1 -2,-2v-0.09A1.65,1.65 0,0 0,9 19.4a1.65,1.65 0,0 0,-1.82 0.33l-0.06,0.06a2,2 0,0 1,-2.83 0,2 2,0 0,1 0,-2.83l0.06,-0.06a1.65,1.65 0,0 0,0.33 -1.82,1.65 1.65,0 0,0 -1.51,-1H3a2,2 0,0 1,-2 -2,2 2,0 0,1 2,-2h0.09A1.65,1.65 0,0 0,4.6 9a1.65,1.65 0,0 0,-0.33 -1.82l-0.06,-0.06a2,2 0,0 1,0 -2.83,2 2,0 0,1 2.83,0l0.06,0.06a1.65,1.65 0,0 0,1.82 0.33H9a1.65,1.65 0,0 0,1 -1.51V3a2,2 0,0 1,2 -2,2 2,0 0,1 2,2v0.09a1.65,1.65 0,0 0,1 1.51,1.65 1.65,0 0,0 1.82,-0.33l0.06,-0.06a2,2 0,0 1,2.83 0,2 2,0 0,1 0,2.83l-0.06,0.06a1.65,1.65 0,0 0,-0.33 1.82V9a1.65,1.65 0,0 0,1.51 1H21a2,2 0,0 1,2 2,2 2,0 0,1 -2,2h-0.09a1.65,1.65 0,0 0,-1.51 1z"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
</vector>''',

    'ic_logout.xml': '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:pathData="M16,17l5,-5l-5,-5"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
    <path
        android:pathData="M21,12H9"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
    <path
        android:pathData="M9,21H5a2,2 0,0 1,-2 -2V5a2,2 0,0 1,2 -2h4"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
</vector>''',

    'ic_search.xml': '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:pathData="M21,21l-4.35,-4.35"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
    <path
        android:pathData="M11,19A8,8 0,1 0,11,3a8,8 0,0 0,0 16z"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
</vector>''',

    'ic_lock.xml': '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:pathData="M5,11h14a2,2 0,0 1,2 2v7a2,2 0,0 1,-2 2H5a2,2 0,0 1,-2 -2v-7a2,2 0,0 1,2 -2z"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
    <path
        android:pathData="M7,11V7a5,5 0,0 1,10 0v4"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
</vector>''',

    'ic_arrow_back.xml': '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:pathData="M12,19l-7,-7l7,-7"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
    <path
        android:pathData="M19,12H5"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
</vector>''',

    'ic_eye.xml': '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:pathData="M2,12s3,-7 10,-7 10,7 10,7 -3,7 -10,7S2,12 2,12z"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
    <path
        android:pathData="M12,15A3,3 0,1 0,12,9a3,3 0,0 0,0 6z"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
</vector>''',

    'ic_eye_off.xml': '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:pathData="M9.88,9.88a3,3 0,1 0,4.24 4.24"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
    <path
        android:pathData="M10.73,5.08A10.43,10.43 0,0 1,12 5c7,0 10,7 10,7a13.16,13.16 0,0 1,-1.67 2.68"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
    <path
        android:pathData="M6.61,6.61A13.52,13.52 0,0 0,2 12s3,7 10,7a9.74,9.74 0,0 0,5.39 -1.61"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
    <path
        android:pathData="M2,2l20,20"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
</vector>'''
}

out_dir = 'app/src/main/res/drawable'
os.makedirs(out_dir, exist_ok=True)
for name, content in vectors.items():
    path = os.path.join(out_dir, name)
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)
    print('Generated VectorDrawable:', name)
