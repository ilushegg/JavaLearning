<#import "parts/common.ftlh" as c>

<@c.page>
    <div class="container">
        <form class="form form_person" method="post" enctype="multipart/form-data">
            <h3 class="form__title">Edit person</h3>
            <div>
                <#if person.filename??>
                    <img class="avatar user_avatar" id="blah" src="/img/${person.filename?ifExists}"  onclick='files.click()' alt="">
                <#else>
                    <img class="avatar user_avatar" src="/img/no_image.png" onclick='files.click()' alt="">
                </#if>
            </div>

            <p>
                <input class="form__input" accept="image/*"  onchange="loadFile(event)" type="file" name="file" id="files" hidden/>
            </p>
            <p>
                <input class="form__input" type="text" name="name" placeholder="Name" value="${person.name?ifExists}"/>
            </p>
            <p>
                <input class="form__input" type="date" name="birthday" placeholder="Birthday" value="${person.birthday?ifExists}"/>
            </p>
                <input type="hidden" name="_csrf" value="${_csrf.token}" />
            <p>
                <input class="button block-item__btn" type="submit" value="Save"/>
            </p>
        </form>
    </div>
</@c.page>