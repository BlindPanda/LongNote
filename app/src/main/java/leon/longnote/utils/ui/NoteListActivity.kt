package leon.longnote.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import leon.longnote.R
import leon.longnote.model.NoteItem
import leon.longnote.presenter.EditNotePresenter
import leon.longnote.utils.InitialDatabase

class NoteListActivity : AppCompatActivity() {
    companion object {
        var mIsActionMode = false
        val REQUEST_INITIAL_PERMISSIONS = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)

        // Add product list fragment if this is first creation
        if (savedInstanceState == null) {
            if (InitialDatabase.firstLaunch(this)) {
                if (checkInitialPermission()) {
                    initialDatabase()
                }
            }

            val fragment = NoteListActivityFragment()

            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment, NoteListActivityFragment.TAG).commit()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            EditNotePresenter.REQUEST_CAMERA_PERMISSIONS -> {
                if (grantResults.isNotEmpty()) {
                    var dienedPermissions = ArrayList<String>()
                    for (permission: String in permissions) {
                        if (grantResults[permissions.indexOf(permission)] != PackageManager.PERMISSION_GRANTED) {
                            dienedPermissions.add(permission)

                        }
                    }
                    if (dienedPermissions.isEmpty()) {
                        val editFragment = supportFragmentManager.findFragmentByTag("editfragment")
                        if (editFragment != null) {
                            (editFragment as EditNoteFragment).getPresenter().onCameraClick()
                        }
                    }
                }
            }
            EditNotePresenter.REQUEST_SELECT_PIC_PERMISSIONS -> {
                if (grantResults.isNotEmpty()) {
                    var dienedPermissions = ArrayList<String>()
                    for (permission: String in permissions) {
                        if (grantResults[permissions.indexOf(permission)] != PackageManager.PERMISSION_GRANTED) {
                            dienedPermissions.add(permission)

                        }
                    }
                    if (dienedPermissions.isEmpty()) {
                        val editFragment = supportFragmentManager.findFragmentByTag("editfragment")
                        if (editFragment != null) {
                            (editFragment as EditNoteFragment).getPresenter().onPictureSelectClick()
                        }
                    }
                }
            }

            EditNotePresenter.REQUEST_AUDIO_PERMISSIONS -> {
                if (grantResults.isNotEmpty()) {
                    var dienedPermissions = ArrayList<String>()
                    for (permission: String in permissions) {
                        if (grantResults[permissions.indexOf(permission)] != PackageManager.PERMISSION_GRANTED) {
                            dienedPermissions.add(permission)

                        }
                    }
                    if (dienedPermissions.isEmpty()) {
                        val editFragment = supportFragmentManager.findFragmentByTag("editfragment")
                        if (editFragment != null) {
                            (editFragment as EditNoteFragment).getPresenter().onAudioClicked()
                        }
                    }
                }
            }
            REQUEST_INITIAL_PERMISSIONS -> {
                if (grantResults.isNotEmpty()) {
                    var dienedPermissions = ArrayList<String>()
                    for (permission: String in permissions) {
                        if (grantResults[permissions.indexOf(permission)] != PackageManager.PERMISSION_GRANTED) {
                            dienedPermissions.add(permission)

                        }
                    }
                    if (dienedPermissions.isEmpty()) {
                        initialDatabase()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.e("yanlonglong", "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode)
        if (requestCode == EditNotePresenter.CAMERA_RESULT && resultCode == Activity.RESULT_OK) {
            if (getEditFragment() != null) {
                if (getEditFragment() is EditNoteFragment) {
                    val photFile = (getEditFragment() as EditNoteFragment).getPresenter().getCameraTakedPhoto()
                    if (photFile != null && photFile.exists()) {
                        (getEditFragment() as EditNoteFragment).getPresenter().handleCameraTakendPhoto()
                    }
                }


            }
        }
        if (EditNotePresenter.REQUEST_CODE_CHOOSE == requestCode && resultCode == Activity.RESULT_OK) {
            if (getEditFragment() != null) {
                if (getEditFragment() is EditNoteFragment) {
                    (getEditFragment() as EditNoteFragment).getPresenter().handleGalleryChoosePhoto(data)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onBackPressed() {
        //super.onBackPressed()
        if (getEditFragment() != null) {
            if (getEditFragment() is EditNoteFragment) {
                (getEditFragment() as EditNoteFragment).getPresenter().onBackPressed()
            } else {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun initialDatabase() {
        (application as BasicApplication).getAppExecutors().diskIO().execute {
            var initialTools = InitialDatabase()
            initialTools.firstLaunchDataB(this, (application as BasicApplication).db)
        }
    }

    /** Shows the product detail fragment  */
    fun show(item: NoteItem?) {
        val editNoteFragment = NoteListActivityFragment.forNote(if (item == null) 0 else item.id!!)
        supportFragmentManager
                .beginTransaction()
                .addToBackStack("editnote")
                .replace(R.id.fragment_container,
                        editNoteFragment, "editfragment").commit()
    }

    fun backToListFragment() {
        //val listNoteFragment = NoteListActivityFragment()
        supportFragmentManager.popBackStack()
    }


    private fun checkInitialPermission(): Boolean {
        val permissionsNeeded = java.util.ArrayList<String>()
        if (!addPermission(android.Manifest.permission.CAMERA))
            permissionsNeeded.add(android.Manifest.permission.CAMERA)
        if (!addPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        return if (permissionsNeeded.size > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsNeeded.toTypedArray(),
                    REQUEST_INITIAL_PERMISSIONS)
            false
        } else {
            true
        }
    }

    private fun addPermission(permission: String): Boolean {
        var result = false
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            result = true
        }
        return result
    }


    private fun getEditFragment(): Fragment? {
        return if (supportFragmentManager.findFragmentById(R.id.fragment_container) != null) supportFragmentManager.findFragmentById(R.id.fragment_container) as Fragment else null
    }


}
