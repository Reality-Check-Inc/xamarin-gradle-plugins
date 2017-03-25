package com.reality_check_inc.xamarin.plugins

import com.reality_check_inc.xamarin.lib.AndroidAppProject
import com.reality_check_inc.xamarin.lib.AndroidLibraryProject
import com.reality_check_inc.xamarin.lib.GenericAppProject
import com.reality_check_inc.xamarin.lib.GenericLibraryProject
import com.reality_check_inc.xamarin.lib.MDToolProject
import com.reality_check_inc.xamarin.lib.XBuildProject
import com.reality_check_inc.xamarin.lib.XamarinProject
import com.reality_check_inc.xamarin.lib.iOSAppProject
import com.reality_check_inc.xamarin.lib.iOSLibraryProject
import org.gradle.api.Project

/**
 * Created by chrisfraser on 7/07/2014.
 */
class XamarinBuildExtension {
    private def Project project
    private def XamarinProject mXamarinProject

    private String mXBuildPath = "xbuild"
    private String mMDToolPath = "/Applications/Xamarin Studio.app/Contents/MacOS/mdtool"

    XamarinBuildExtension(Project prj) {
        project = prj
    }

    private def setProject(XamarinProject xprj, Closure closure) {
        if (this.mXamarinProject != null)
            throw new Exception("You must only define one Xamarin project per Gradle project!")

        project.configure(xprj, closure)
        this.mXamarinProject = xprj
    }

    XamarinProject getXamarinProject() {
        mXamarinProject
    }

    def xbuildPath(String xbuildPath) {
        mXBuildPath = xbuildPath
    }

    def getXbuildPath() {
        return mXBuildPath
    }

    def mdtoolPath(String mdtoolpath) {
        mMDToolPath = mdtoolpath
    }

    def getMdtoolPath() {
        return mMDToolPath
    }

    def androidAppProject(Closure closure) {
        setProject(new AndroidAppProject(project), closure)
    }

    def androidLibraryProject(Closure closure) {
        setProject(new AndroidLibraryProject(project), closure)
    }

    def iOSAppProject(Closure closure) {
        setProject(new iOSAppProject(project), closure)
    }

    def iOSLibraryProject(Closure closure) {
        setProject(new iOSLibraryProject(project), closure)
    }

    def genericLibraryProject(Closure closure) {
        setProject(new GenericLibraryProject(project), closure)
    }

    def genericAppProject(Closure closure) {
        setProject(new GenericAppProject(project), closure)
    }

    def xbuildProject(Closure closure) {
        setProject(new XBuildProject(project), closure)
    }

    def mdtoolProject(Closure closure) {
        setProject(new MDToolProject(project), closure)
    }
}
