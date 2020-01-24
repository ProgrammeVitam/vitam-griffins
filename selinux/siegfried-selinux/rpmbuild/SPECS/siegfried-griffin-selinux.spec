%global selinuxtype	targeted
%global moduletype	contrib
%global modulename	vitam_siegfried_griffin

Name: vitam-siegfried-griffin-selinux
Version: 1.0
Release: 1%{?dist}
Summary: SELinux security policy module vitam-siegfried-griffin
License: CeCILL 2.1
URL:     https://github.com/ProgrammeVitam/vitam
Source0: %{modulename}.fc
Source1: %{modulename}.te
Source2: Makefile
BuildArch: noarch
BuildRequires: selinux-policy
BuildRequires: selinux-policy-devel
Requires: vitam-siegfried-griffin
Requires: policycoreutils-python

%description
SELinux security policy module vitam-siegfried-griffin

%prep
rm -rf vitam_siegfried_griffin*
cp %{SOURCE0} %{SOURCE1} %{SOURCE2} .

%build
make

%install
install -d %{buildroot}%{_datadir}/selinux/packages
install -m 0644 %{modulename}.pp.bz2 %{buildroot}%{_datadir}/selinux/packages
bzip2 -d %{buildroot}%{_datadir}/selinux/packages/%{modulename}.pp.bz2

%post
# Install the module
semodule -i %{_datadir}/selinux/packages/vitam_siegfried_griffin.pp
# If it's an update, remove managed ports before adding them again
# if [ $1 -gt 1 ]; then
#    semanage port -D -t vitam_siegfried_griffin_port_t
# fi
# Relabel
restorecon -R /vitam/bin/worker/griffins/siegfried-griffin
restorecon -R /vitam/tmp/worker/griffins/siegfried-griffin

%postun
# If it's a real uninstall (not an update), remove everything
if [ $1 -eq 0 ]; then
#    semanage port -D -t vitam_siegfried_griffin_port_t
    semodule -r vitam_siegfried_griffin
    restorecon -R /vitam/bin/worker/griffins/siegfried-griffin
    restorecon -R /vitam/tmp/worker/griffins/siegfried-griffin
fi

%files
%attr(0644,root,root) %{_datadir}/selinux/packages/%{modulename}.pp

%doc


%changelog
* Fri Oct 18 2019 French Prime minister Office/SGMAP/DINSIC/Vitam Program <contact.vitam@culture.gouv.fr>
- Initial version
