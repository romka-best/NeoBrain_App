"""Обновил базу

Revision ID: 50f3f53ff9aa
Revises: 13029f24bfa1
Create Date: 2020-06-05 23:25:32.201055

"""
from alembic import op
import sqlalchemy as sa

# revision identifiers, used by Alembic.
revision = '50f3f53ff9aa'
down_revision = '13029f24bfa1'
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    pass
    # op.drop_table('achievement_association')
    # op.drop_table('post_association')
    # op.drop_table('app_association')
    # op.drop_column('messages', 'with_attachments')
    # op.drop_constraint(None, 'posts', type_='foreignkey')
    # op.drop_column('posts', 'user_id')
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.add_column('posts', sa.Column('user_id', sa.INTEGER(), nullable=True))
    op.create_foreign_key(None, 'posts', 'users', ['user_id'], ['id'])
    op.add_column('messages', sa.Column('with_attachments', sa.BOOLEAN(), nullable=True))
    op.create_table('app_association',
                    sa.Column('user_id', sa.INTEGER(), nullable=False),
                    sa.Column('app_id', sa.INTEGER(), nullable=False),
                    sa.ForeignKeyConstraint(['app_id'], ['apps.id'], ),
                    sa.ForeignKeyConstraint(['user_id'], ['users.id'], ),
                    sa.PrimaryKeyConstraint('user_id', 'app_id')
                    )
    op.create_table('post_association',
                    sa.Column('user_id', sa.INTEGER(), nullable=False),
                    sa.Column('post_id', sa.INTEGER(), nullable=False),
                    sa.Column('like_emoji', sa.BOOLEAN(), nullable=True),
                    sa.Column('laughter_emoji', sa.BOOLEAN(), nullable=True),
                    sa.Column('heart_emoji', sa.BOOLEAN(), nullable=True),
                    sa.Column('disappointed_emoji', sa.BOOLEAN(), nullable=True),
                    sa.Column('smile_emoji', sa.BOOLEAN(), nullable=True),
                    sa.Column('angry_emoji', sa.BOOLEAN(), nullable=True),
                    sa.Column('smile_with_heart_eyes', sa.BOOLEAN(), nullable=True),
                    sa.Column('screaming_emoji', sa.BOOLEAN(), nullable=True),
                    sa.Column('is_author', sa.BOOLEAN(), nullable=True),
                    sa.CheckConstraint('angry_emoji IN (0, 1)'),
                    sa.CheckConstraint('disappointed_emoji IN (0, 1)'),
                    sa.CheckConstraint('heart_emoji IN (0, 1)'),
                    sa.CheckConstraint('is_author IN (0, 1)'),
                    sa.CheckConstraint('laughter_emoji IN (0, 1)'),
                    sa.CheckConstraint('like_emoji IN (0, 1)'),
                    sa.CheckConstraint('screaming_emoji IN (0, 1)'),
                    sa.CheckConstraint('smile_emoji IN (0, 1)'),
                    sa.CheckConstraint('smile_with_heart_eyes IN (0, 1)'),
                    sa.ForeignKeyConstraint(['post_id'], ['posts.id'], ),
                    sa.ForeignKeyConstraint(['user_id'], ['users.id'], ),
                    sa.PrimaryKeyConstraint('user_id', 'post_id')
                    )
    op.create_table('achievement_association',
                    sa.Column('user_id', sa.INTEGER(), nullable=False),
                    sa.Column('achievement_id', sa.INTEGER(), nullable=False),
                    sa.Column('is_get', sa.BOOLEAN(), nullable=True),
                    sa.CheckConstraint('is_get IN (0, 1)'),
                    sa.ForeignKeyConstraint(['achievement_id'], ['achievements.id'], ),
                    sa.ForeignKeyConstraint(['user_id'], ['users.id'], ),
                    sa.PrimaryKeyConstraint('user_id', 'achievement_id')
                    )
    # ### end Alembic commands ###
