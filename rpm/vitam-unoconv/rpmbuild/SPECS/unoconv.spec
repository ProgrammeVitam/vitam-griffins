Name:          vitam-unoconv
Version:       1.0.0
Release:       1%{?dist}
Summary:       Tool to convert between any document format supported by LibreOffice
Group:         System Environment/Base
License:       GPL
BuildArch:     x86_64
Source0:       unoconv

BuildArch:     noarch
BuildRequires: python >= 2.0
Requires:      python >= 2.0
Requires:      libreoffice = 6.0.7
Requires:      libreoffice-pyuno = 6.0.7


%description
unoconv converts between any document format that LibreOffice understands.
It uses LibreOffice's UNO bindings for non-interactive conversion of
documents.

Supported document formats include: Open Document Text (.odt),
Open Document Draw (.odd), Open Document Presentation (.odp),
Open Document calc (.odc), MS Word (.doc), MS PowerPoint (.pps/.ppt),
MS Excel (.xls), MS Office Open/OOXML (.xml),
Portable Document Format (.pdf), DocBook (.xml), LaTeX (.ltx),
HTML, XHTML, RTF, Docbook (.xml), GIF, PNG, JPG, SVG, BMP, EPS
and many more...

%prep

%install
mkdir -p %{buildroot}/usr/bin/
cp %{SOURCE0} %{buildroot}/usr/bin/

%pre

%post
setsebool -P httpd_execmem on

%preun

%postun

%clean
rm -rf %{buildroot}

%files
%defattr(-,root,root,-)

%attr(755, vitam, vitam) /usr/bin/unoconv

%doc


%changelog
* Tue Jan 22 2019 French Prime minister Office/SGMAP/DINSIC/Vitam Program <contact.vitam@culture.gouv.fr>
- Initial version
